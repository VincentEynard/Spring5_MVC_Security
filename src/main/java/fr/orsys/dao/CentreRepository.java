package fr.orsys.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import fr.orsys.entities.Centre;
import fr.orsys.entities.Formation;

public interface CentreRepository extends JpaRepository<Centre, Long>{

	public Centre findByNom(String nom);
	
	public Page<Centre> findAll(Pageable pageable);

	public boolean existsByNom(String nom);
	


}
