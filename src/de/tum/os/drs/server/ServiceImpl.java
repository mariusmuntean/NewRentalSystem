package de.tum.os.drs.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.commons.collections.map.HashedMap;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.tum.os.drs.client.IClientService;
import de.tum.os.drs.client.helpers.CookieHelper;
import de.tum.os.drs.client.helpers.OAuthApiHelper;
import de.tum.os.drs.client.helpers.OAuthParser;
import de.tum.os.drs.client.model.DeviceType;
import de.tum.os.drs.client.model.EventType;
import de.tum.os.drs.client.model.OAuthAuthorities;
import de.tum.os.drs.client.model.PersistentDevice;
import de.tum.os.drs.client.model.PersistentEvent;
import de.tum.os.drs.client.model.PersistentRenter;
import de.tum.os.drs.client.model.RentalSession;
import de.tum.os.drs.client.model.SerializableRenter;
import de.tum.os.drs.server.helpers.JSONHelper;

public class ServiceImpl extends RemoteServiceServlet implements IClientService {

	private static final String PERSISTENCE_UNIT_NAME = "rentalsystem";
	private static EntityManagerFactory factory;
	/**
	 * A mapping from SessionIdHash to RentalSession objects. This is used to 
	 * keep track of logged in users.
	 */
	private HashMap<Integer, RentalSession> currentSessions = new HashMap<Integer, RentalSession>();
	
	boolean waitingForResult = true;
	Boolean currentTokenValidity = null;

	/**
	 * Set of authorized IDs.
	 * Note, the client doesn't provide the ID but only a token. The server then 
	 * uses the token to get the ID e.g. from Google, if the ID is in this list the 
	 * user is authorized.
	 */
	private static final HashSet<String> authorizedIDs = new HashSet<String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5869829676400912163L;
		{
			add("100005386531690");
			add("1251677124");
			add("iphoneiswhatineed@gmail.com"); // Test account
			add("tumrentalsystem@gmail.com"); // Google developer account that manages the OAuth2 stuff
			add("fachgebietf13@googlemail.com"); // Frau Guggenmos email
			add("114987213862005858495"); // Nils T. Google ID
			add("112327375720464453708"); // Marius Google ID
			add("53493968ff2c2533"); // Marius Live ID
		}
	};

	public ServiceImpl() {
		super();
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

		// createDummyData();

		System.out.println("Servlet constructor done!");
	}

	@Override
	public RentalSession login(String token, OAuthAuthorities authority) {
		Boolean validToken = checkTokenValiditySync(token, authority);
		RentalSession newSession = new RentalSession(UUID.randomUUID().toString());
		if (validToken) {
			newSession.setIsValid(true);
		} else {
			newSession.setIsValid(false);
		}
		
		// Keep track of it
		this.currentSessions.put(newSession.getSessionIdHash(), newSession);
		
		return newSession;
	}

	@Override
	public Boolean logout(int sessionIdHash) {
		if (this.currentSessions == null || this.currentSessions.keySet().size() == 0) {
			return false;
		}
		if (this.currentSessions.containsKey(sessionIdHash) == false
				|| this.currentSessions.get(sessionIdHash) == null) {
			return false;
		}

		try {
			this.currentSessions.remove(sessionIdHash);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void createDummyData() {

		EntityManager em = factory.createEntityManager();

		Random rand = new Random();
		em.getTransaction().begin();
		for (int i = 0; i < 3; i++) {

			PersistentDevice pd = new PersistentDevice(
					String.valueOf(rand.nextDouble() * 1000000000), "Nexus X",
					"Mistery Nexus", "as new?!", DeviceType.Smartphone, "???" + i
							+ ".png", i > 0.5 ? true : false, new Date());
			System.out.println("Generated new devices: " + pd.toString());
			em.persist(pd);

		}
		em.getTransaction().commit();
		em.close();
	}

	/*
	 * IClientService implementation START
	 */

	@Override
	public ArrayList<PersistentDevice> getAllDevices(int sessionIdHash) {
			
		if (ValidateSession(sessionIdHash) == false) {
			return null;
		}

		EntityManager em = factory.createEntityManager();
		Query q = em.createQuery("select p from PersistentDevice p");
		ArrayList<PersistentDevice> pds = new ArrayList<PersistentDevice>(
				q.getResultList());
		for (PersistentDevice pd : pds) {
			System.out.println(pd.toString());
		}
		System.out.println("Size: " + pds.size());

		return (ArrayList<PersistentDevice>) pds;
	}

	@Override
	public PersistentDevice getDeviceByImei(int sessionIdHash, String imei) {
		if (ValidateSession(sessionIdHash) == false) {
			return null;
		}

		EntityManager em = factory.createEntityManager();
		Query q = em.createQuery("select p from PersistentDevice p where p.IMEI = :imei");
		q.setParameter("imei", imei);
		PersistentDevice pd = (PersistentDevice) q.getSingleResult();

		return pd;
	}

	@Override
	public ArrayList<PersistentDevice> getDevicesbyImei(int sessionIdHash,
			String[] imeiCodes) {
		if (ValidateSession(sessionIdHash) == false) {
			return null;
		}

		EntityManager em = factory.createEntityManager();
		Query q = em
				.createQuery("select p from PersistentDevice p where p.IMEI IN :imeiCodes");
		q.setParameter("imeiCodes", imeiCodes);
		ArrayList<PersistentDevice> pds = (ArrayList<PersistentDevice>) q.getResultList();

		return pds;
	}

	@Override
	public ArrayList<PersistentDevice> getAvailableDevices(int sessionIdHash) {
		if (ValidateSession(sessionIdHash) == false) {
			return null;
		}

		EntityManager em = factory.createEntityManager();
		Query q = em
				.createQuery("select p from PersistentDevice p where p.isAvailable = true");
		ArrayList<PersistentDevice> pds = new ArrayList<PersistentDevice>(
				q.getResultList());
		for (PersistentDevice pd : pds) {
			System.out.println(pd.toString());
		}
		System.out.println("Size: " + pds.size());

		return (ArrayList<PersistentDevice>) pds;
	}

	@Override
	public ArrayList<PersistentDevice> getRentedDevices(int sessionIdHash) {
		if (ValidateSession(sessionIdHash) == false) {
			return null;
		}

		EntityManager em = factory.createEntityManager();
		Query q = em
				.createQuery("select p from PersistentDevice p where p.isAvailable = false");
		ArrayList<PersistentDevice> pds = new ArrayList<PersistentDevice>(
				q.getResultList());
		for (PersistentDevice pd : pds) {
			System.out.println(pd.toString());
		}
		System.out.println("Size: " + pds.size());

		return (ArrayList<PersistentDevice>) pds;
	}

	@Override
	public Boolean addNewDevice(int sessionIdHash, PersistentDevice device) {
		if (ValidateSession(sessionIdHash) == false) {
			return null;
		}

		EntityManager em = factory.createEntityManager();

		try {

			em.getTransaction().begin();

			em.persist(device);

			em.getTransaction().commit();

		} catch (Exception e) {
			em.close();
			return new Boolean(false);
		}
		em.close();
		return new Boolean(true);

	}

	@Override
	public Boolean updateDeviceInfo(int sessionIdHash, PersistentDevice device) {
		if (ValidateSession(sessionIdHash) == false) {
			return null;
		}

		EntityManager em = factory.createEntityManager();

		try {

			em.getTransaction().begin();

			em.merge(device);

			em.getTransaction().commit();

		} catch (Exception e) {
			em.close();
			return new Boolean(false);
		}
		em.close();
		return new Boolean(true);

	}

	@Override
	public Boolean deleteDevice(int sessionIdHash, PersistentDevice device) {
		return deleteDevice(sessionIdHash, device.getIMEI());
	}

	@Override
	public Boolean deleteDevice(int sessionIdHash, String imei) {
		if (ValidateSession(sessionIdHash) == false) {
			return null;
		}

		EntityManager em = factory.createEntityManager();

		try {

			em.getTransaction().begin();

			Query q = em
					.createQuery("DELETE FROM PersistentDevice p WHERE p.IMEI = :imei");

			q.setParameter("imei", imei);
			int noOfDeletedObjects = q.executeUpdate();
			System.out.println("Deleted " + noOfDeletedObjects + " devices");

			em.getTransaction().commit();

		} catch (Exception e) {
			em.close();
			return new Boolean(false);
		}
		em.close();
		return new Boolean(true);

	}

	@Override
	public Boolean addRenter(int sessionIdHash, SerializableRenter renter) {
		if (ValidateSession(sessionIdHash) == false) {
			return null;
		}

		EntityManager em = factory.createEntityManager();
		PersistentRenter pr = pRenterFromSrenter(renter);
		try {

			em.getTransaction().begin();

			em.persist(pr);

			em.getTransaction().commit();

			System.out.println("Added new renter: " + pr.toString());

		} catch (Exception e) {
			em.close();
			return new Boolean(false);
		}
		em.close();
		return new Boolean(true);
	}

	@Override
	public SerializableRenter getRenter(int sessionIdHash, String mattriculationNumber) {
		if (ValidateSession(sessionIdHash) == false) {
			return null;
		}

		EntityManager em = factory.createEntityManager();
		Query q = em
				.createQuery("select p from PersistentRenter p where p.matriculationNumber = :matrNr");
		q.setParameter("matrNr", mattriculationNumber);
		PersistentRenter pr = (PersistentRenter) q.getSingleResult();

		SerializableRenter sr = sRenterFromPrenter(pr);
		System.out.println("Got Renter by Matr Nr. :" + sr.toString());

		return sr;
	}

	@Override
	public ArrayList<SerializableRenter> getAllRenters(int sessionIdHash) {
		if (ValidateSession(sessionIdHash) == false) {
			return null;
		}

		EntityManager em = factory.createEntityManager();
		Query q = em.createQuery("select p from PersistentRenter p");
		ArrayList<PersistentRenter> prs = new ArrayList<PersistentRenter>(
				q.getResultList());
		ArrayList<SerializableRenter> srs = new ArrayList<SerializableRenter>(prs.size());
		for (PersistentRenter pr : prs) {
			System.out.println("Getting renter: " + pr.toString());
			srs.add(sRenterFromPrenter(pr));
		}
		System.out.println("Size: " + prs.size());

		return srs;
	}
	
	@Override
	public ArrayList<SerializableRenter> getAllActiveRenters(int sessionIdHash) {
		if (ValidateSession(sessionIdHash) == false) {
			return null;
		}

		EntityManager em = factory.createEntityManager();
		Query q = em.createQuery("select distinct p from PersistentRenter p JOIN p.rentedDevices r");
		ArrayList<PersistentRenter> prs = new ArrayList<PersistentRenter>(
				q.getResultList());
		ArrayList<SerializableRenter> srs = new ArrayList<SerializableRenter>(prs.size());
		System.out.println("Returning active renters: " + prs.size());
		for (PersistentRenter pr : prs) {
			System.out.println("Getting active renter: " + pr.toString());
			srs.add(sRenterFromPrenter(pr));
		}
		

		return srs;
	}

	@Override
	public Boolean deleteRenter(int sessionIdHash, SerializableRenter renter) {
		if (ValidateSession(sessionIdHash) == false) {
			return null;
		}

		EntityManager em = factory.createEntityManager();

		try {
			// Get a reference to the PersistentRenter corresponding to this SerializableRenter.
			Query q = em
					.createQuery("select p from PersistentRenter p where p.matriculationNumber = :matrNr");
			q.setParameter("matrNr", renter.getMatriculationNumber());
			PersistentRenter pr = (PersistentRenter) q.getSingleResult();

			// ... and then remove it.
			em.getTransaction().begin();

			em.remove(pr);

			em.getTransaction().commit();

		} catch (Exception e) {
			em.close();
			return new Boolean(false);
		}
		em.close();
		return new Boolean(true);
	}

	@Override
	public Boolean deleteRenter(int sessionIdHash, String matriculationNumber) {
		if (ValidateSession(sessionIdHash) == false) {
			return null;
		}

		EntityManager em = factory.createEntityManager();

		try {

			em.getTransaction().begin();

			Query q = em
					.createQuery("DELETE FROM PersistentRenter p WHERE p.matriculationNumber = :matrNr");
			q.setMaxResults(1);
			q.setParameter("matrNr", matriculationNumber);
			int noOfDeletedObjects = q.executeUpdate();
			System.out.println("Deleted " + noOfDeletedObjects + " devices");

			em.getTransaction().commit();

		} catch (Exception e) {
			em.close();
			return new Boolean(false);
		}
		em.close();
		return new Boolean(true);
	}

	@Override
	public Boolean updateRenter(int sessionIdHash, String matriculationNumber,
			SerializableRenter renter) {
		if (ValidateSession(sessionIdHash) == false) {
			return null;
		}

		EntityManager em = factory.createEntityManager();

		try {
			// Get a reference to the PersistentRenter corresponding to the provided SerializableRenter
			Query q = em
					.createQuery("select p from PersistentRenter p where p.matriculationNumber = :matrNr");
			q.setParameter("matrNr", matriculationNumber);
			PersistentRenter pr = (PersistentRenter) q.getSingleResult();

			// ... then update its fields with the data from the SerializableRenter
			updatePersistentRenterFromSerializableRenter(pr, renter);

			// ... and finally persist it.
			em.getTransaction().begin();

			em.merge(pr);

			em.getTransaction().commit();

		} catch (Exception e) {
			em.close();
			return new Boolean(false);
		}
		em.close();
		return new Boolean(true);
	}

	@Override
	public ArrayList<PersistentDevice> getDevicesRentedBy(int sessionIdHash,
			String renterMatriculationNumber) {
		if (ValidateSession(sessionIdHash) == false) {
			return null;
		}

		EntityManager em = factory.createEntityManager();

		// Get a reference to the PersistentRenter
		PersistentRenter pr = getPersistentRenterFromMatrNr(em, renterMatriculationNumber);

		// Get an array of Strings with the IMEI codes of the rented devices.
		String[] rentedDevicesImeiCodes = new String[pr.getRentedDevices().size()];
		rentedDevicesImeiCodes = pr.getRentedDevices().toArray(rentedDevicesImeiCodes);

		// Query for devices with IMEI codes in that list.
		Query devQuery = em
				.createQuery("SELECT d FROM PersistentDevice d WHERE d.IMEI IN :imeiCodes");
		devQuery.setParameter("imeiCodes", rentedDevicesImeiCodes);

		ArrayList<PersistentDevice> result = new ArrayList<PersistentDevice>(
				devQuery.getResultList());

		return result;
	}

	@Override
	public Boolean rentDeviceTo(int sessionIdHash, String renterMatrNr,
			String deviceImeiCode, Date estimatedReturnDate, String comments,
			String signatureHTML) {
		if (ValidateSession(sessionIdHash) == false) {
			return null;
		}

		EntityManager em = factory.createEntityManager();
		try {
			// Add device to list of rented devices
			PersistentRenter pr = getPersistentRenterFromMatrNr(em, renterMatrNr);
			pr.getRentedDevices().add(deviceImeiCode);
			updateEntity(em, pr);

			// Mark device as rented
			PersistentDevice pd = getPersistentDeviceFromIMEI(em, deviceImeiCode);
			pd.setIsAvailable(false);
			pd.setEstimatedReturnDate(estimatedReturnDate);
			updateEntity(em, pd);

			// Log this event.
			PersistentEvent pe = new PersistentEvent(pr.getName(),
					pr.getMatriculationNumber(), pr.getEmail(), pd.getName(),
					pd.getIMEI(), EventType.Rented, comments, signatureHTML);
			persistEntity(em, pe);

			em.close();
		} catch (Exception e) {
			em.close();
			return false;
		}

		return true;
	}

	@Override
	public boolean rentDevicesTo(int sessionIdHash, String renterMatrNr,
			String[] imeiCodes, Date estimatedReturnDate, String comments,
			String signatureHTML) {
		if (ValidateSession(sessionIdHash) == false) {
			return false;
		}

		EntityManager em = factory.createEntityManager();
		try {
			// Add devices to list of rented devices.
			PersistentRenter pr = getPersistentRenterFromMatrNr(em, renterMatrNr);
			for (String deviceImeiCode : imeiCodes) {
				pr.getRentedDevices().add(deviceImeiCode);
			}
			updateEntity(em, pr);

			// Mark devices as rented and Log events.
			em.getTransaction().begin();
			for (String deviceImeiCode : imeiCodes) {
				PersistentDevice pd = getPersistentDeviceFromIMEI(em, deviceImeiCode);
				pd.setIsAvailable(false);
				pd.setEstimatedReturnDate(estimatedReturnDate);
				em.merge(pd);

				PersistentEvent pe = new PersistentEvent(pr.getName(),
						pr.getMatriculationNumber(), pr.getEmail(), pd.getName(),
						pd.getIMEI(), EventType.Rented, comments, signatureHTML);
				em.persist(pe);
			}
			em.getTransaction().commit();

			em.close();
		} catch (Exception e) {
			em.close();
			return false;
		}

		return true;
	}

	@Override
	public Boolean returnDevice(int sessionIdHash, String renterMatrNr,
			String deviceImeiCode, String comments, String signatureHTML) {
		if (ValidateSession(sessionIdHash) == false) {
			return null;
		}

		EntityManager em = factory.createEntityManager();
		try {
			// Delete returned device from list of rented devices.
			PersistentRenter pr = getPersistentRenterFromMatrNr(em, renterMatrNr);
			pr.getRentedDevices().remove(deviceImeiCode);
			updateEntity(em, pr);

			// Mark returned device as available.
			PersistentDevice pd = getPersistentDeviceFromIMEI(em, deviceImeiCode);
			pd.setIsAvailable(true);
			updateEntity(em, pd);

			// Log this event.
			PersistentEvent pe = new PersistentEvent(pr.getName(),
					pr.getMatriculationNumber(), pr.getEmail(), pd.getName(),
					pd.getIMEI(), EventType.Returned, comments, signatureHTML);
			persistEntity(em, pe);

			em.close();
		} catch (Exception e) {
			em.close();
			return false;
		}

		return true;
	}

	@Override
	public Boolean returnDevices(int sessionIdHash, String renterMatrNr,
			String[] imeiCodes, String comments, String signatureHTML) {
		if (ValidateSession(sessionIdHash) == false) {
			return null;
		}

		EntityManager em = factory.createEntityManager();
		try {
			// Delete returned devices from list of rented devices.
			PersistentRenter pr = getPersistentRenterFromMatrNr(em, renterMatrNr);
			for (String deviceImeiCode : imeiCodes) {
				pr.getRentedDevices().remove(deviceImeiCode);
			}
			updateEntity(em, pr);

			// Mark returned devices as available and log events.
			em.getTransaction().begin();
			for (String deviceImeiCode : imeiCodes) {
				PersistentDevice pd = getPersistentDeviceFromIMEI(em, deviceImeiCode);
				pd.setIsAvailable(true);
				em.merge(pd);

				PersistentEvent pe = new PersistentEvent(pr.getName(),
						pr.getMatriculationNumber(), pr.getEmail(), pd.getName(),
						pd.getIMEI(), EventType.Returned, comments, signatureHTML);
				em.persist(pe);
			}
			em.getTransaction().commit();

			em.close();
		} catch (Exception e) {
			em.close();
			return false;
		}

		return true;
	}

	@Override
	public ArrayList<PersistentEvent> getEvents(int sessionIdHash, String personName,
			String IMEI, Date from, Date to, Integer maxResultSize,
			Boolean reverseChronologicalOrder) {
		if (ValidateSession(sessionIdHash) == false) {
			return null;
		}

		EntityManager em = factory.createEntityManager();

		StringBuilder sb = new StringBuilder();
		sb.append("select e from PersistentEvent e");

		if (personName != null || IMEI != null || from != null || to != null)
			sb.append(" WHERE 1=1");

		if (personName != null && personName.length() > 0)
			sb.append(" AND e.persName = '" + personName + "'");
		if (IMEI != null && IMEI.length() > 0)
			sb.append(" AND e.devImeiCode = '" + IMEI + "'");
		if (from != null)
			sb.append(" AND e.eventDate >= " + dateToJpaDateString(from));
		if (to != null)
			sb.append(" AND e.eventDate <= " + dateToJpaDateString(to));

		if (reverseChronologicalOrder) {
			sb.append(" ORDER BY e.eventDate DESC");
		} else {
			sb.append(" ORDER BY e.eventDate ASC");
		}

		Query q = em.createQuery(sb.toString());
		if (maxResultSize != Integer.MAX_VALUE)
			q.setMaxResults(maxResultSize);
		ArrayList<PersistentEvent> pes = new ArrayList<PersistentEvent>(q.getResultList());

		return pes;
	}

	/*
	 * IClientService implementation END
	 */

	/*
	 * Helper methods region.
	 */

	private String dateToJpaDateString(Date date) {

		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd' 'hh:mm:ss");
		return "'" + ft.format(date) + "'";

	}

	private <T> void updateEntity(EntityManager em, T entity) {

		em.getTransaction().begin();

		em.merge(entity);

		em.getTransaction().commit();

	}

	private <T> void persistEntity(EntityManager em, T entity) {

		em.getTransaction().begin();

		em.persist(entity);

		em.getTransaction().commit();

	}

	/**
	 * 
	 * @param matrNr
	 * @return
	 */
	private PersistentRenter getPersistentRenterFromMatrNr(EntityManager em, String matrNr) {
		Query q = em
				.createQuery("select p from PersistentRenter p where p.matriculationNumber = :matrNr");
		q.setParameter("matrNr", matrNr);
		PersistentRenter pr = (PersistentRenter) q.getSingleResult();
		return pr;
	}

	private PersistentDevice getPersistentDeviceFromIMEI(EntityManager em, String imei) {
		Query q = em.createQuery("select d from PersistentDevice d where d.IMEI = :imei");
		q.setParameter("imei", imei);
		PersistentDevice pd = (PersistentDevice) q.getSingleResult();
		return pd;
	}

	/**
	 * Generates a SerializableRenter object from the PersistentRenter param.
	 * 
	 * @param pr
	 *            - the PersistentRenter object who's data should be transmitted over the wire.
	 * @return - a serializable DAO.
	 */
	private SerializableRenter sRenterFromPrenter(PersistentRenter pr) {
		SerializableRenter sr = new SerializableRenter(pr.getName(),
				pr.getMatriculationNumber(), pr.getEmail(), pr.getPhoneNumber(),
				pr.getComments(), new ArrayList<String>(pr.getRentedDevices()));

		return sr;

	}

	/**
	 * Generates a PersistentRenter object from the SerializableRenter parameter.
	 * 
	 * @param sr
	 *            - the SerializableRenter object who's data should be persisted.
	 * @return - a persistable object (JPA Entity).
	 */
	private PersistentRenter pRenterFromSrenter(SerializableRenter sr) {
		PersistentRenter pr = new PersistentRenter();
		pr.setName(sr.getName());
		pr.setMatriculationNumber(sr.getMatriculationNumber());
		pr.setEmail(sr.getEmail());
		pr.setPhoneNumber(sr.getPhoneNUmber());

		ArrayList<String> rentedDevices = sr.getRentedDevices();
		if (rentedDevices != null && rentedDevices.size() > 0)
			pr.setRentedDevices(new ArrayList<String>(rentedDevices));

		return pr;

	}

	/**
	 * Updates the given PersistentRenter with the info from the SerializableRenter
	 * 
	 * @param pr
	 * @param sr
	 * @throws Exception
	 */
	private void updatePersistentRenterFromSerializableRenter(PersistentRenter pr,
			SerializableRenter sr) throws Exception {
		if (pr == null || sr == null)
			throw new Exception(
					"Parameters of updatePersistentRenterFromSerializablerenter cannot be null!");

		pr.setName(sr.getName());
		pr.setComments(sr.getComments());
		pr.setEmail(sr.getEmail());
		pr.setMatriculationNumber(sr.getMatriculationNumber());
		pr.setPhoneNumber(sr.getPhoneNUmber());
		pr.setRentedDevices(sr.getRentedDevices());
	}

	private Boolean checkTokenValiditySync(String token, OAuthAuthorities authority) {

		if (token != null && !token.isEmpty()) {
			String tokenCheckUrl = OAuthApiHelper.getAuthUrlFromAuthority(authority);

			if (tokenCheckUrl == null || tokenCheckUrl.length() <= 0) {
				return false;
			}

			// Append token
			tokenCheckUrl += token;
			// Test token validity at the appropriate Authority
			try {
				// Retrieve json response
				URL url = new URL(tokenCheckUrl);
				URLConnection connection = url.openConnection();

				String line;
				StringBuilder builder = new StringBuilder();
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				String jsonString = builder.toString();

				// Analyze json response
				if (JSONHelper.hasError(jsonString)) {
					currentTokenValidity = false;
				} else {
					String userID = JSONHelper.getUserId(jsonString);
					String userEmail = JSONHelper.getUserEmail(jsonString);
					if (isAuthorizedId(userID) || isAuthorizedId(userEmail)) {
						currentTokenValidity = true;
					} else {
						currentTokenValidity = false;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			currentTokenValidity = false;
		}

		return currentTokenValidity;
	}

	private Boolean checkTokenvalidity(String token, OAuthAuthorities authority) {

		if (token != null && !token.isEmpty()) {
			String tokenCheckUrl = OAuthApiHelper.getAuthUrlFromAuthority(authority);

			if (tokenCheckUrl == null || tokenCheckUrl.length() <= 0) {
				return false;
			}

			// Append token
			tokenCheckUrl += token;
			// Test token validity at the appropriate Authority
			RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, tokenCheckUrl);
			try {
				// Just a small hack to make the call seem synchronous
				waitingForResult = true;
				// Check token validity
				Request req = rb.sendRequest(null, new RequestCallback() {

					@Override
					public void onResponseReceived(Request request, Response response) {
						String jsonString = response.getText();
						if (OAuthParser.hasError(jsonString)) {
							currentTokenValidity = false;
						} else {
							String userID = OAuthParser
									.getAuthenticatedUserID(jsonString);
							String userEmail = OAuthParser
									.getAuthenticatedUserEmail(jsonString);
							if (isAuthorizedId(userID) || isAuthorizedId(userEmail)) {
								currentTokenValidity = true;
							} else {
								currentTokenValidity = false;
							}
						}

						waitingForResult = false;
					}

					@Override
					public void onError(Request request, Throwable exception) {
						waitingForResult = false;
					}
				});

				while (waitingForResult) {
					Thread.sleep(50);
				}

			} catch (Exception e) {

			}

		} else {
			currentTokenValidity = false;
		}

		return currentTokenValidity;
	}

	protected boolean isAuthorizedId(String value) {
		if (value == null || value.isEmpty()) {
			return false;
		}

		if (authorizedIDs.contains(value.toLowerCase())) {
			return true;
		} else {
			return false;
		}

	}
	/**
	 * Utility method. Checks if the provided hash code is from
	 * a valid session.
	 * 
	 * @param sessionIdHash - the hash code to check.
	 * @return - Returns true if the hash code is mapped to 
	 * an existing valid session, false otherwise.
	 */
	private boolean ValidateSession(int sessionIdHash) {
		// If no such hash is mapped or if it is mapped to null (it can happen) return null
		if (!this.currentSessions.containsKey(sessionIdHash)
				|| this.currentSessions.get(sessionIdHash) == null) {
			return false;
		}
		// OK, so there is a session mapped to this hash, check whether or not it is still valid
		if (!this.currentSessions.get(sessionIdHash).getIsValid()) {
			return false;
		}

		return true;
	}

}
