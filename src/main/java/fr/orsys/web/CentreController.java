package fr.orsys.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

import fr.orsys.entities.Centre;
import fr.orsys.entities.Formateur;
import fr.orsys.entities.Formation;
import fr.orsys.entities.FormationInter;
import fr.orsys.entities.FormationIntra;
import fr.orsys.metier.OrsysMetier;

@Controller
public class CentreController {
	@Autowired
	private OrsysMetier orsysMetier;

	@RequestMapping("/user/centres")
	public String centres(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "2") int size,
			@RequestParam(name = "errorMessage", defaultValue = "") String errorMessage) {
		Page<Centre> listCentres = orsysMetier.getAllCentresPageable(page, size);
		model.addAttribute("activePage", page);
		model.addAttribute("size", size);
		int[] taillePagination = IntStream.range(0, listCentres.getTotalPages()).toArray();
		model.addAttribute("taillePagination", taillePagination);
		model.addAttribute("listeCentres", listCentres);
		model.addAttribute("errorMessage", errorMessage);
		model.addAttribute("formFormation", new Formation());
		return "/centres";
	}

	@RequestMapping("/admin/ajoutercentre")
	public String ajoutercentre(Model model) {
		model.addAttribute("formCentre", new Centre());
		return "/ajoutercentre";
	}

	@PostMapping("/admin/ajoutercentre")
	public String ajoutercentre(@Valid @ModelAttribute(value = "formCentre") Centre formCentre, BindingResult result,
			Model model) {
		if (orsysMetier.existsCentre(formCentre.getNom())) {
			result.rejectValue("nom", "error.formCentre", "Le nom existe déjà!");
		}
		if (result.hasErrors()) {
			return "/ajoutercentre";
		} else {
			orsysMetier.saveCentre(formCentre);
			return "redirect:/user/centres";
		}
	}

	@RequestMapping(value = "/admin/modifiercentre", method = RequestMethod.GET)
	public String modifiercentre(Model model, @RequestParam(name = "id", defaultValue = "0") Long id) {
		Centre oldCentre = orsysMetier.getCentreById(id);
		model.addAttribute("formCentre", oldCentre);
		return "/modifiercentre";
	}

	@PostMapping("/admin/modifiercentre")
	public String modifiercentre(@Valid @ModelAttribute(value = "formCentre") Centre formCentre, BindingResult result) {
		if (result.hasErrors()) {
			return "/modifiercentre";
		} else {
			orsysMetier.saveCentre(formCentre);
			return "redirect:/user/centres";
		}
	}

	@RequestMapping("/admin/supprimercentre")
	public String supprimercentre(@RequestParam(name = "id", defaultValue = "0") Long id) {
		Centre c = orsysMetier.getCentreById(id);
		try {
			orsysMetier.deleteCentre(c);
			return "redirect:/user/centres";
		} catch (Exception e) {
			String msg = "Vous ne pouvez pas supprimer un centre contenant des formations";
			return "redirect:/centres?errorMessage=" + msg;
		}
	}

}
