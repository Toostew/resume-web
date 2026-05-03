package com.toostew.resume_web.DAO;


import com.toostew.resume_web.entity.Post;
import com.toostew.resume_web.exception.ControllerException;
import com.toostew.resume_web.exception.DAOException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public class PostDAO {

    private EntityManager em;

    public PostDAO(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public void create(Post post) {
        try {
            em.persist(post);
        } catch(EntityExistsException e) {
            throw new ControllerException("Issue in PostDAO: Post already exists", e);
        } catch (Exception e) {
            throw new ControllerException("Issue in PostDAO: Unknown Issue", e);
        }

    }

    public Post read(int id) {
        try{
            return em.find(Post.class, id);
        } catch (EntityNotFoundException e) {
            throw new DAOException("Issue in PostDAO: id doesn't exist", e);
        } catch (Exception e) {
            throw new DAOException("Issue in PostDAO: unknown issue", e);
        }
    }

    @Transactional
    public void update(Post post) {
        try{
            Post temp = em.find(Post.class, post.getId());
            temp.setContent(post.getContent());
            temp.setDescription(post.getDescription());
            temp.setThumbnail(post.getThumbnail());
            temp.setTitle(post.getTitle());
            temp.setTitleURLFriendly(post.getTitleURLFriendly());
            temp.setUploadDate(post.getUploadDate());
            em.merge(temp);
            System.out.println("Updating file: " + post.getId());
        } catch (EntityNotFoundException e) {
            throw new DAOException("Issue in FileDAO: id doesn't exist", e);
        } catch (Exception e) {
            throw new DAOException("Issue in FileDAO: unknown issue", e);
        }
    }

    @Transactional
    public void delete(int id) {
        try{
            Post temp = em.find(Post.class, id);
            em.remove(temp);
        } catch (EntityNotFoundException e) {
            throw new DAOException("Issue in PostDAO: id doesn't exist", e);
        } catch (Exception e) {
            throw new DAOException("Issue in PostDAO: unknown issue", e);
        }
    }

}
