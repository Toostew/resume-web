package com.toostew.resume_web.DAO;

import com.toostew.resume_web.entity.R2File;
import com.toostew.resume_web.exception.DAOException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class FileDAO {

    private EntityManager em;

    public FileDAO(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public R2File createFile(R2File r2File) {
        em.persist(r2File);
        return  r2File;
    }

    public R2File readFile(int id) {
        try{
            return em.find(R2File.class, id);
        } catch (EntityNotFoundException e) {
            throw new DAOException("Issue in FileDAO: id doesn't exist", e);
        } catch (Exception e) {
            throw new DAOException("Issue in FileDAO: unknown issue", e);
        }
    }

    //return all files
    public List<R2File> readAllFiles() {
        try {
            // We create a query selecting "f" from the R2File entity
            List<R2File> files = em.createQuery("SELECT f FROM R2File f", R2File.class)
                    .getResultList();

            // Convert the List to a Set and return
            return files;

        } catch (Exception e) {
            throw new DAOException("Issue in FileDAO: Could not retrieve all files", e);
        }
    }

    @Transactional
    public void updateFile(R2File r2File) {
        try{
            R2File temp = em.find(R2File.class, r2File.getId());
            temp.setContent_type(r2File.getContent_type());
            temp.setSize(r2File.getSize());
            temp.setDate_created(r2File.getDate_created());
            temp.setStored_name(r2File.getStored_name());
            temp.setOriginal_name(r2File.getOriginal_name());
            em.merge(temp);
            System.out.println("Updating file: " + r2File.getId());
        } catch (EntityNotFoundException e) {
            throw new DAOException("Issue in FileDAO: id doesn't exist", e);
        } catch (Exception e) {
            throw new DAOException("Issue in FileDAO: unknown issue", e);
        }
    }

    @Transactional
    public void deleteFile(int id) {
        try{
            R2File temp = em.find(R2File.class, id);
            em.remove(temp);
        } catch (EntityNotFoundException e) {
            throw new DAOException("Issue in FileDAO: id doesn't exist", e);
        } catch (Exception e) {
            throw new DAOException("Issue in FileDAO: unknown issue", e);
        }
    }



}
