package com.toostew.resume_web.DAO;


import com.toostew.resume_web.entity.Post;
import com.toostew.resume_web.exception.ControllerException;
import com.toostew.resume_web.exception.DAOException;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

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
            throw new DAOException("Issue in postDAO: id doesn't exist", e);
        } catch (Exception e) {
            throw new DAOException("Issue in postDAO: unknown issue", e);
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

    //get total number of posts
    public long getTotalNumberOfPosts(){
        try{
            return em.createQuery("select count(p) from Post p", Long.class).getSingleResult();
        }catch(Exception e){
            throw new DAOException("Issue in PostDAO: couldn't get total number of posts, unknown issue", e);
        }
    }

    //get all posts
    //literally, no safeguards. For the love of god please oh please implement pagination in the future you sick bastard
    public List<Post> getAllPosts() {
        try{
            TypedQuery<Post> query = em.createQuery("from Post p", Post.class);
            List<Post> postList = query.getResultList();
            return postList;
        } catch (Exception e){
            throw new DAOException("Issue in PostDAO: couldn't get total number of posts, unknown issue", e);
        }

    }

    public Post getLatestPost() {
        try {
            return em.createQuery("SELECT p FROM Post p ORDER BY p.uploadDate DESC, p.id DESC", Post.class)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // Handle case where table is empty
        } catch (Exception e) {
            throw new DAOException("Issue in PostDAO: Could not return latest post, unknown issue", e);
        }
    }


}
