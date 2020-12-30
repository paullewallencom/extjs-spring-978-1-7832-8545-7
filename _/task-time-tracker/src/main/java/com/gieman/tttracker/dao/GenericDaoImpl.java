package com.gieman.tttracker.dao;

import com.gieman.tttracker.domain.EntityItem;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class GenericDaoImpl<T, ID extends Serializable> implements GenericDao<T, ID> {

    final protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @PersistenceContext(unitName = "tttPU")
    protected EntityManager em;
    private final Class<T> type;

    public GenericDaoImpl(Class<T> type1) {
        this.type = type1;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public T find(ID id) {
        return (T) em.find(type, id);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void persist(T o) {
        em.persist(o);
        // insert to the DB now so that the ID is populated
        // If not flushed, subsequent actions that require a ID
        // may fail (depends on when transaction is committed)

        em.flush(); // was added for testing of ID inserted in Ch 5

        /**
         * The following code is one possible solution for the Exercise in
         * Chapter 4 of the book
         */
        if (o instanceof EntityItem) {
            EntityItem<ID> item = (EntityItem<ID>) o;
            ID id = item.getId();
            logger.info("The " + o.getClass().getName() + " record with ID=" + id + " has been inserted");
        }
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public T merge(T o) {

        o = em.merge(o);
        //em.flush();
        return o;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void remove(T o) {

        // associate object with persistence context
        o = merge(o);
        em.remove(o);

        /**
         * The following code is one possible solution for the Exercise in
         * Chapter 4 of the book
         */
        if (o instanceof EntityItem) {
            EntityItem<ID> item = (EntityItem<ID>) o;
            ID id = item.getId();
            logger.warn("The " + o.getClass().getName() + " record with ID=" + id + " has been deleted");
        }
    }
}
