package com.toostew.resume_web.DAO;

import com.toostew.resume_web.entity.Thumbnail;
import com.toostew.resume_web.exception.DAOException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public class ThumbnailDAO {


    private EntityManager em;

    public ThumbnailDAO(EntityManager em) {
        this.em = em;
    }



    //returns thumbnail but with ID
    @Transactional
    public Thumbnail create(Thumbnail thumbnail) {
        em.persist(thumbnail);
        return  thumbnail;
    }

    public Thumbnail readFile(int id) {
        try{
            return em.find(Thumbnail.class, id);
        } catch (EntityNotFoundException e) {
            throw new DAOException("Issue in FileDAO: id doesn't exist", e);
        } catch (Exception e) {
            throw new DAOException("Issue in FileDAO: unknown issue", e);
        }
    }

    @Transactional
    public void update(Thumbnail thumbnail) {
        try{
            Thumbnail temp = em.find(Thumbnail.class, thumbnail.getId());
            temp.setContent_type(thumbnail.getContent_type());
            temp.setSize(thumbnail.getSize());
            temp.setDate_created(thumbnail.getDate_created());
            temp.setStored_name(thumbnail.getStored_name());
            temp.setOriginal_name(thumbnail.getOriginal_name());
            em.merge(temp);
            System.out.println("Updating file: " + thumbnail.getId());
        } catch (EntityNotFoundException e) {
            throw new DAOException("Issue in FileDAO: id doesn't exist", e);
        } catch (Exception e) {
            throw new DAOException("Issue in FileDAO: unknown issue", e);
        }
    }

    @Transactional
    public void delete(int id) {
        try{
            Thumbnail temp = em.find(Thumbnail.class, id);
            em.remove(temp);
        } catch (EntityNotFoundException e) {
            throw new DAOException("Issue in ThumbnailDAO: id doesn't exist", e);
        } catch (Exception e) {
            throw new DAOException("Issue in ThumbnailDAO: unknown issue", e);
        }
    }
}
