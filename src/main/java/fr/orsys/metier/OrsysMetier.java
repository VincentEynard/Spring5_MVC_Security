package fr.orsys.metier;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Page;

import fr.orsys.entities.AppRole;
import fr.orsys.entities.AppUser;
import fr.orsys.entities.Centre;
import fr.orsys.entities.Formateur;
import fr.orsys.entities.Formation;
import fr.orsys.entities.FormationInter;
import fr.orsys.entities.FormationIntra;
import fr.orsys.entities.Participant;
import java.util.Optional;


public interface OrsysMetier {
	
	//Consulter une formation par son code
	public Formation findByCode(String code);

	//Consulter un participant par son age
	public Participant findByAge(Integer age);

	public List<Formation> getAllFormation();

	public Page<Formation> getAllFormationsPageable(int page, int size);

	public List<fr.orsys.entities.Centre> getAllCentres();

	public List<fr.orsys.entities.Formateur> getAllFormateurs();

	public void saveFormation(@Valid Formation formFormation);
	

	public boolean existsFormation(String code);

	public Optional<Formation> getFormation(Long id);

	public Page<Participant> getAllParticipantsByFormation(Formation f, int page, int size);

	public void deleteFormation(Formation f);

	public Page<Centre> getAllCentresPageable(int page, int size);

	public Centre findByNom(String nom);

	public boolean existsCentre(String nom);

	public void saveCentre(@Valid Centre formCentre);

	public Centre getCentreById(Long id);

	public void deleteCentre(Centre c);

	public Page<Formateur> getAllFormateursPageable(int page, int size);

	public boolean existsFormateurByNomAndPrenom(String nom, String prenom);
	public void saveFormateur(@Valid Formateur formFormateur);
	public Formateur getFormateurById(Long id);
	public void deleteFormateur(Formateur c);
	public boolean existsParticipantByNomAndPrenom(String nom, String prenom);
	public Page<Participant> getAllParticipantsPageable(int page, int size);
	public void saveParticipant(@Valid Participant formParticipant);
	public Participant getParticipantById(Long id);
	public void deleteParticipant(Participant c);

	
	/************************Security************************/
	public AppUser saveUser(AppUser user);
	public AppRole saveRole(AppRole role);
	public void addRoleToUser(String username,String roleName);
	public AppUser findUserByUsername(String username);











	

}
