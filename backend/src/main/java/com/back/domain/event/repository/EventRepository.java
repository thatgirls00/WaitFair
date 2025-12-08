package com.back.domain.event.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.back.domain.event.entity.Event;
import com.back.domain.event.entity.EventCategory;
import com.back.domain.event.entity.EventStatus;

public interface EventRepository extends JpaRepository<Event, Long> {

	@Query("SELECT e FROM Event e WHERE e.id = :id AND e.deleted = false")
	Optional<Event> findById(@Param("id") Long id);

	@Query("SELECT e FROM Event e WHERE e.deleted = false ORDER BY e.createAt DESC")
	Page<Event> findAllByOrderByCreateAtDesc(Pageable pageable);

	@Query("SELECT e FROM Event e WHERE e.status = :status AND e.deleted = false ORDER BY e.createAt DESC")
	Page<Event> findByStatusOrderByCreateAtDesc(@Param("status") EventStatus status, Pageable pageable);

	@Query("SELECT e FROM Event e WHERE e.category = :category AND e.deleted = false ORDER BY e.createAt DESC")
	Page<Event> findByCategoryOrderByCreateAtDesc(@Param("category") EventCategory category, Pageable pageable);

	@Query("SELECT e FROM Event e WHERE e.status = :status AND e.category = :category AND e.deleted = false ORDER BY e.createAt DESC")
	Page<Event> findByStatusAndCategoryOrderByCreateAtDesc(
		@Param("status") EventStatus status,
		@Param("category") EventCategory category,
		Pageable pageable);

	@Query("SELECT e FROM Event e WHERE "
		+ "(:status IS NULL OR e.status = :status) AND "
		+ "(:category IS NULL OR e.category = :category) AND "
		+ "e.deleted = false "
		+ "ORDER BY e.createAt DESC")
	Page<Event> findByConditions(
		@Param("status") EventStatus status,
		@Param("category") EventCategory category,
		Pageable pageable);

	@Query("SELECT e FROM Event e WHERE e.status = :status AND e.deleted = false")
	List<Event> findByStatus(@Param("status") EventStatus status);
}
