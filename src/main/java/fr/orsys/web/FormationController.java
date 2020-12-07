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
import fr.orsys.entities.Participant;
import fr.orsys.metier.OrsysMetier;

@Controller
public class FormationController {
	@Autowired
	private OrsysMetier orsysMetier;

	@RequestMapping("")
	public String index() {
		return "redirect:/user/formations";
	}

	@RequestMapping("/user/formations")
	public String formations(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "2") int size,
			@RequestParam(name = "errorMessage", defaultValue = "") String errorMessage) {
		Page<Formation> listFormations = orsysMetier.getAllFormationsPageable(page, size);
		model.addAttribute("activePage", page);
		model.addAttribute("size", size);
		int[] taillePagination = IntStream.range(0, listFormations.getTotalPages()).toArray();
		model.addAttribute("taillePagination", taillePagination);
		model.addAttribute("listeFormations", listFormations);
		model.addAttribute("errorMessage", errorMessage);
		model.addAttribute("formFormation", new Formation());
		return "/formations";
	}

	@PostMapping("/user/rechercheformation")
	public String rechercheformation(@Valid @ModelAttribute(value = "formFormation") Formation formFormation,
			BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "/formations";
		} else {
			Formation formation = orsysMetier.findByCode(formFormation.getCode());
			List<Formation> listFormations = new ArrayList<Formation>();
			if (formation != null)
				listFormations.add(formation);
			System.out.println(listFormations);
			model.addAttribute("activePage", 0);
			model.addAttribute("size", 1);
			model.addAttribute("taillePagination", 0);
			model.addAttribute("listeFormations", listFormations);
			model.addAttribute("errorMessage", "");
			return "/formations";
		}
	}

	@RequestMapping(value = "/admin/ajouterformationIntra")
	public String ajouterformationIntra(Model model) {
		model.addAttribute("formFormation", new FormationIntra());
		List<Centre> listeCentres = orsysMetier.getAllCentres();
		model.addAttribute("listeCentres", listeCentres);
		List<Formateur> listeFormateurs = orsysMetier.getAllFormateurs();
		model.addAttribute("listeFormateurs", listeFormateurs);
		return "/ajouterformationintra";
	}

	@PostMapping("/admin/ajouterformationIntra")
	public String ajouterformationIntra(@Valid @ModelAttribute(value = "formFormation") FormationIntra formFormation,
			BindingResult result, Model model) {
		if (orsysMetier.existsFormation(formFormation.getCode())) {
			result.rejectValue("code", "error.formFormation", "Le code existe déjà!");
		}
		if (result.hasErrors()) {
			List<Centre> listeCentres = orsysMetier.getAllCentres();
			model.addAttribute("listeCentres", listeCentres);
			List<Formateur> listeFormateurs = orsysMetier.getAllFormateurs();
			model.addAttribute("listeFormateurs", listeFormateurs);
			return "/ajouterformationintra";
		} else {
			orsysMetier.saveFormation(formFormation);
			return "redirect:/formations";
		}
	}

	@RequestMapping(value = "/admin/ajouterformationInter")
	public String ajouterformationInter(Model model) {
		model.addAttribute("formFormation", new FormationInter());
		List<Centre> listeCentres = orsysMetier.getAllCentres();
		model.addAttribute("listeCentres", listeCentres);
		List<Formateur> listeFormateurs = orsysMetier.getAllFormateurs();
		model.addAttribute("listeFormateurs", listeFormateurs);
		return "/ajouterformationinter";
	}

	@PostMapping(value = "/admin/ajouterformationInter")
	public String ajouterformationInter(@Valid @ModelAttribute(value = "formFormation") FormationInter formFormation,
			BindingResult result, Model model) {
		if (orsysMetier.existsFormation(formFormation.getCode())) {
			result.rejectValue("code", "error.formFormation", "Le code existe déjà!");
		}
		if (formFormation.getNbEntreprises() < 1 || formFormation.getNbEntreprises() > 10) {
			result.rejectValue("nbEntreprises", "error.formFormation",
					"Le nombre d'entreprise doit être entre 1 et 10!");
		}
		if (result.hasErrors()) {
			List<Centre> listeCentres = orsysMetier.getAllCentres();
			model.addAttribute("listeCentres", listeCentres);
			List<Formateur> listeFormateurs = orsysMetier.getAllFormateurs();
			model.addAttribute("listeFormateurs", listeFormateurs);
			return "/ajouterformationinter";
		} else {
			orsysMetier.saveFormation(formFormation);
			return "redirect:/user/formations";
		}
	}

	@RequestMapping(value = "/admin/modifierformation", method = RequestMethod.GET)
	public String modifierformation(Model model, @RequestParam(name = "id", defaultValue = "0") Long id) {
		List<Centre> listeCentres = orsysMetier.getAllCentres();
		model.addAttribute("listeCentres", listeCentres);
		List<Formateur> listeFormateurs = orsysMetier.getAllFormateurs();
		model.addAttribute("listeFormateurs", listeFormateurs);
		// Récupérer le type de formation
		Optional<Formation> formationOp = orsysMetier.getFormation(id);
		Formation oldFormation = formationOp.get();
		if (Hibernate.getClass(oldFormation).getSimpleName().equals("FormationInter")) {
			FormationInter oldFormationInter = (FormationInter) formationOp.get();
			model.addAttribute("formFormation", oldFormationInter);
			return "/modifierformationinter";
		} else {
			FormationIntra oldFormationIntra = (FormationIntra) formationOp.get();
			model.addAttribute("formFormation", oldFormationIntra);
			return "/modifierformationintra";
		}
	}

	@PostMapping("/admin/modifierformationIntra")
	public String modifierformationIntra(@Valid @ModelAttribute(value = "formFormation") FormationIntra formFormation,
			BindingResult result, Model model) {
		if (result.hasErrors()) {
			List<Centre> listeCentres = orsysMetier.getAllCentres();
			model.addAttribute("listeCentres", listeCentres);
			List<Formateur> listeFormateurs = orsysMetier.getAllFormateurs();
			model.addAttribute("listeFormateurs", listeFormateurs);
			return "/modifierformationintra";
		} else {
			orsysMetier.saveFormation(formFormation);
			return "redirect:/user/formations";
		}
	}

	@PostMapping("/admin/modifierformationInter")
	public String modifierformationInter(@Valid @ModelAttribute(value = "formFormation") FormationInter formFormation,
			BindingResult result, Model model) {
		if (formFormation.getNbEntreprises() < 1 || formFormation.getNbEntreprises() > 10) {
			result.rejectValue("nbEntreprises", "error.formFormation",
					"Le nombre d'entreprise doit être entre 1 et 10!");
		}
		if (result.hasErrors()) {
			List<Centre> listeCentres = orsysMetier.getAllCentres();
			model.addAttribute("listeCentres", listeCentres);
			List<Formateur> listeFormateurs = orsysMetier.getAllFormateurs();
			model.addAttribute("listeFormateurs", listeFormateurs);
			return "/modifierformationinter";
		} else {
			orsysMetier.saveFormation(formFormation);
			return "redirect:/user/formations";
		}
	}

	@RequestMapping(value = "/user/consulterformation")
	public String consulterformation(Model model, @RequestParam(name = "id", defaultValue = "0") Long id,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "2") int size) {
		Optional<Formation> fOp = orsysMetier.getFormation(id);
		Formation f = fOp.get();
		Page<Participant> listParticipants = orsysMetier.getAllParticipantsByFormation(f, page, size);
		model.addAttribute("activePage", page);
		model.addAttribute("size", size);
		int[] taillePagination = IntStream.range(0, listParticipants.getTotalPages()).toArray();
		model.addAttribute("taillePagination", taillePagination);
		model.addAttribute("listeParticipants", listParticipants);
		model.addAttribute("formation", f);
		return "/consulterformation";
	}

	@RequestMapping(value = "/admin/supprimerformation")
	public String supprimerformation(@RequestParam(name = "id", defaultValue = "0") Long id) {
		Optional<Formation> fOp = orsysMetier.getFormation(id);
		Formation f = fOp.get();
		try {
			orsysMetier.deleteFormation(f);
			return "redirect:/user/formations";
		} catch (Exception e) {
			String msg = "Vous ne pouvez pas supprimer une formation contenant des participants";
			return "redirect:/user/formations?errorMessage=" + msg;
		}
	}

}
