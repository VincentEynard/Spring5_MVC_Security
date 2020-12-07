package fr.orsys.entities;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name= "TYPE_FORM", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("MERE")
@Data @AllArgsConstructor @NoArgsConstructor
public class Formation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private Long id;
	@NotNull(message = "Vous devez remplir le champ code!")
	@NotEmpty(message = "Le code ne doit pas être vide!") 
	@NotBlank(message = "Le code ne doit pas contenir d espace!") 
	@Size(min = 3, max=5, message = "La code doit contenir entre 3 et 5 caractères!")
	private String code;
	private String theme;
	private double prix;
	@ManyToOne 
	private Formateur formateur;
	@ManyToOne 	
	private Centre centre;
	@ManyToMany (mappedBy = "formation", fetch=FetchType.LAZY)
	private Collection<Participant> participant;
	public Long getId() {
		return id;
	}

}
