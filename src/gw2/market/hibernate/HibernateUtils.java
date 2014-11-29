package gw2.market.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class HibernateUtils {

	private static SessionFactory sessionFactory;
	
	protected static SessionFactory getSessionFactory(){
		if(sessionFactory == null){
			sessionFactory = new Configuration().configure().buildSessionFactory();
		}
		return sessionFactory;
	}
	
	public static Transaction beginTransaction(){
		return getSessionFactory().getCurrentSession().beginTransaction();
	}
	
	public static Session getSession(){
		return getSessionFactory().getCurrentSession();
	}
	
	public static void close(){
		sessionFactory.close();
	}
	
	public static class AutoCommit implements AutoCloseable{
		private Session session;
		private Transaction transaction;
		public AutoCommit(){
			this.session = HibernateUtils.getSession();
			this.transaction = HibernateUtils.beginTransaction();
		}
		
		public Session getSession(){
			return session;
		}
		
		@Override
		public void close() throws Exception {
			transaction.commit();
		}
	}
}
