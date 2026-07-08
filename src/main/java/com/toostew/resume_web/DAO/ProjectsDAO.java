package com.toostew.resume_web.DAO;

import com.toostew.resume_web.entity.Projects;
import com.toostew.resume_web.exception.DAOException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProjectsDAO {

    private EntityManager em;

    public ProjectsDAO(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public Projects createProjects(Projects projects) {
        em.persist(projects);
        return projects;
    }

    public Projects readProjects(int id) {
        try{
            return em.find(Projects.class, id);
        } catch (EntityNotFoundException e) {
            throw new DAOException("Issue in ProjectsDAO: id doesn't exist", e);
        } catch (Exception e) {
            throw new DAOException("Issue in ProjectsDAO: unknown issue", e);
        }
    }

    //return all files, sorted by id (newer first)
    public List<Projects> readAllProjects() {
        try {
            // We create a query selecting "f" from the Projects entity
            List<Projects> files = em.createQuery("from Projects p order by p.id desc", Projects.class)
                    .getResultList();

            // Convert the List to a Set and return
            return files;

        } catch (Exception e) {
            throw new DAOException("Issue in ProjectsDAO: Could not retrieve all files", e);
        }
    }

    //get featured Projects, sorted by ID so newer IDs first
    public List<Projects> getFeaturedProjectsOrLatest() {
        try {
            TypedQuery<Projects> query = em.createQuery(
                    "from Projects p where p.featured = true order by p.id desc", Projects.class);
            List<Projects> files = query.getResultList();
            if (files.isEmpty()) { //if there are no featured, return just the
                List<Projects> all = readAllProjects(); // already sorted newest-first
                return all.isEmpty() ? List.of() : List.of(all.get(0)); //return ONLY a list of the first item
            }
            return files;
        } catch (Exception e) {
            throw new DAOException("Issue in ProjectDAO: couldn't get featured projects, unknown issue", e);
        }
    }

    //get non-featured Projects, sorted by ID so newer IDs first
    public List<Projects> getNonFeaturedProjects() {
        try {
            TypedQuery<Projects> query = em.createQuery(
                    "from Projects p where p.featured = false order by p.id desc", Projects.class);
            List<Projects> files = query.getResultList();

            return files;
        } catch (Exception e) {
            throw new DAOException("Issue in ProjectDAO: couldn't get featured projects, unknown issue", e);
        }
    }

    @Transactional
    public void updateProjects(Projects r2Projects) {
        try{
            Projects temp = em.find(Projects.class, r2Projects.getId());



            em.merge(temp);
            System.out.println("Updating file: " + r2Projects.getId());
        } catch (EntityNotFoundException e) {
            throw new DAOException("Issue in ProjectsDAO: id doesn't exist", e);
        } catch (Exception e) {
            throw new DAOException("Issue in ProjectsDAO: unknown issue", e);
        }
    }

    @Transactional
    public void deleteProjects(int id) {
        try{
            Projects temp = em.find(Projects.class, id);
            em.remove(temp);
        } catch (EntityNotFoundException e) {
            throw new DAOException("Issue in ProjectsDAO: id doesn't exist", e);
        } catch (Exception e) {
            throw new DAOException("Issue in ProjectsDAO: unknown issue", e);
        }
    }
    
    
}
