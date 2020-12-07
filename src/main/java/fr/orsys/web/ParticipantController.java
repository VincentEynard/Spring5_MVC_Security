package fr.orsys.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.validation.Valid;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import fr.orsys.entities.Formation;
import fr.orsys.entities.Participant;
import fr.orsys.metier.OrsysMetier;

@Controller
public class ParticipantController {
	@Autowired
	private OrsysMetier orsysMetier;

	@RequestMapping("/user/participants")
	public String participants(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "2") int size,
			@RequestParam(name = "errorMessage", defaultValue = "") String errorMessage) {
		Page<Participant> listParticipants = orsysMetier.getAllParticipantsPageable(page, size);
		model.addAttribute("activePage", page);
		model.addAttribute("size", size);
		int[] taillePagination = IntStream.range(0, listParticipants.getTotalPages()).toArray();
		model.addAttribute("taillePagination", taillePagination);
		model.addAttribute("listeParticipants", listParticipants);
		model.addAttribute("errorMessage", errorMessage);
		model.addAttribute("formParticipant", new Participant());
		return "/participants";
	}

	@RequestMapping("/admin/ajouterparticipant")
	public String ajouterparticipant(Model model) {
		// Appel la liste des formations
		model.addAttribute("listeFormation", orsysMetier.getAllFormation());
		model.addAttribute("formParticipant", new Participant());
		return "/ajouterparticipant";
	}

	@PostMapping("/admin/ajouterparticipant")
	public String ajouterparticipant(@Valid @ModelAttribute(value = "formParticipant") Participant formParticipant,
			BindingResult result, Model model) {
		if (orsysMetier.existsParticipantByNomAndPrenom(formParticipant.getNom(), formParticipant.getPrenom())) {
			result.rejectValue("nom", "error.formCentre", "Ce participant existe déja.");
		}
		if (result.hasErrors()) {
			return "/admin/ajouterparticipant";
		} else {
			orsysMetier.saveParticipant(formParticipant);
			return "redirect:/user/participants";
		}
	}

	@RequestMapping(value = "/admin/modifierparticipant", method = RequestMethod.GET)
	public String modifierparticipant(Model model, @RequestParam(name = "id", defaultValue = "0") Long id) {
		Participant formParticipant = orsysMetier.getParticipantById(id);
		List<Formation> listeFormation = orsysMetier.getAllFormation();
		List<Long> ids = formParticipant.getFormation().stream().map(Formation::getId).collect(Collectors.toList());
		System.out.println(ids);
		model.addAttribute("selectedFormation", ids);
		model.addAttribute("listeFormation", listeFormation);
		model.addAttribute("formParticipant", formParticipant);
		return "/modifierparticipant";
	}

	@PostMapping("/admin/modifierparticipant")
	public String modifierparticipant(@Valid @ModelAttribute(value = "formParticipant") Participant formParticipant,
			BindingResult result) {
		if (result.hasErrors()) {
			return "modifierparticipant";
		} else {
			orsysMetier.saveParticipant(formParticipant);
			return "redirect:/user/participants";
		}
	}

	@RequestMapping("/admin/supprimerparticipant")
	public String supprimerparticipant(@RequestParam(name = "id", defaultValue = "0") Long id) {
		Participant c = orsysMetier.getParticipantById(id);
		try {
			orsysMetier.deleteParticipant(c);
			return "redirect:/user/participants";
		} catch (Exception e) {
			String msg = "Vous ne pouvez pas supprimer ce participant";
			return "redirect:/user/participants?errorMessage=" + msg;
		}
	}

}
