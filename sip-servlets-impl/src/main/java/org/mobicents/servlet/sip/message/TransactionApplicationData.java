/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2014, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package org.mobicents.servlet.sip.message;

import java.io.Serializable;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.sip.Address;
import javax.sip.Transaction;
import javax.sip.address.Hop;

import org.apache.log4j.Logger;
import org.mobicents.servlet.sip.core.message.MobicentsTransactionApplicationData;
import org.mobicents.servlet.sip.core.session.MobicentsSipSessionKey;
import org.mobicents.servlet.sip.proxy.ProxyBranchImpl;

/**
 * A container for holding branch specific data.
 * 
 *@author mranga
 */
public class TransactionApplicationData implements Serializable, MobicentsTransactionApplicationData {		

	private static final long serialVersionUID = 9170581635026591070L;
	private static final Logger logger = Logger.getLogger(TransactionApplicationData.class);
	private AtomicBoolean messageCleanedUp; 
	private ProxyBranchImpl proxyBranch;	
	private SipServletMessageImpl sipServletMessage;
	private String method;
	private MobicentsSipSessionKey sipSessionKey;
	private Set<SipServletResponseImpl> sipServletResponses;
	private transient Transaction transaction;
	private transient String initialRemoteHostAddress;
	private transient int initialRemotePort;
	private transient String initialRemoteTransport;
	private transient Address initialPoppedRoute;
	private transient AtomicInteger rseqNumber;
	// to be made non transient if we support tx failover at some point
	// or handle it conditionally through an Externalizable interface
	private transient String appNotDeployed = null;
	private transient boolean noAppReturned = false;
	private transient String modifier = null;	
	private transient boolean canceled = false;
	// Used for RFC 3263
	private transient Queue<Hop> hops = null;
	
	public TransactionApplicationData(SipServletMessageImpl sipServletMessage ) {		
		this.sipServletMessage = sipServletMessage;
		this.sipSessionKey = sipServletMessage.getSipSessionKey();
		sipServletResponses = null;
		// Added for https://github.com/RestComm/sip-servlets/issues/107
		messageCleanedUp = new AtomicBoolean(false);
	}
	
	public void setProxyBranch(ProxyBranchImpl proxyBranch) {
		this.proxyBranch = proxyBranch;
	}
	
	/**
	 * @return the proxyBranch
	 */
	public ProxyBranchImpl getProxyBranch() {
		return proxyBranch;
	}
	
	public SipServletMessageImpl getSipServletMessage() {
		return this.sipServletMessage;
	}
	/**
	 * @return the transaction
	 */
	public Transaction getTransaction() {
		return transaction;
	}
	/**
	 * @param transaction the transaction to set
	 */
	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}
	
	/**
	 * used to get access from the B2BUA to pending messages on the transaction
	 */
	public void addSipServletResponse(SipServletResponseImpl sipServletResponse) {
		if(sipServletResponses == null) {
			sipServletResponses = new CopyOnWriteArraySet<SipServletResponseImpl>();
		}
		sipServletResponses.add(sipServletResponse);
	}
	
	public Set<SipServletResponseImpl> getSipServletResponses() {
		return sipServletResponses;
	}
	/**
	 * @param initialRemoteHostAddress the initialRemoteHostAddress to set
	 */
	public void setInitialRemoteHostAddress(String initialRemoteHostAddress) {
		this.initialRemoteHostAddress = initialRemoteHostAddress;
	}
	/**
	 * @return the initialRemoteHostAddress
	 */
	public String getInitialRemoteHostAddress() {
		return initialRemoteHostAddress;
	}
	/**
	 * @param initialRemotePort the initialRemotePort to set
	 */
	public void setInitialRemotePort(int initialRemotePort) {
		this.initialRemotePort = initialRemotePort;
	}
	/**
	 * @return the initialRemotePort
	 */
	public int getInitialRemotePort() {
		return initialRemotePort;
	}
	/**
	 * @param initialRemoteTransport the initialRemoteTransport to set
	 */
	public void setInitialRemoteTransport(String initialRemoteTransport) {
		this.initialRemoteTransport = initialRemoteTransport;
	}
	/**
	 * @return the initialRemoteTransport
	 */
	public String getInitialRemoteTransport() {
		return initialRemoteTransport;
	}
	public Address getInitialPoppedRoute() {
		return initialPoppedRoute;
	}
	/**
	 * @param initialPoppedRoute the initialPoppedRoute to set
	 */
	public void setInitialPoppedRoute(Address initialPoppedRoute) {
		this.initialPoppedRoute = initialPoppedRoute;
	}
	
	/**
	 * @return the rseqNumber
	 */
	public AtomicInteger getRseqNumber() {
		if(rseqNumber == null) {
			rseqNumber = new AtomicInteger(1);
		}
		return rseqNumber;
	}
	/**
	 * @param rseqNumber the rseqNumber to set
	 */
	public void setRseqNumber(AtomicInteger rseqNumber) {
		this.rseqNumber = rseqNumber;
	}

	/**
	 * @param appNotDeployed the appNotDeployed to set
	 */
	public void setAppNotDeployed(String appNotDeployed) {
		this.appNotDeployed = appNotDeployed;
	}

	/**
	 * @return the appNotDeployed
	 */
	public String getAppNotDeployed() {
		return appNotDeployed;
	}

	/**
	 * @param noAppReturned the noAppReturned to set
	 */
	public void setNoAppReturned(boolean noAppReturned) {
		this.noAppReturned = noAppReturned;
	}

	/**
	 * @return the noAppReturned
	 */
	public boolean isNoAppReturned() {
		return noAppReturned;
	}
	
	public void setSipServletMessage(SipServletMessageImpl message) {
		this.sipServletMessage = message;
	}


	/**
	 * @param modifier the modifier to set
	 */
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	/**
	 * @return the modifier
	 */
	public String getModifier() {
		return modifier;
	}
	
	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	public void cleanUp() {
		if(logger.isDebugEnabled()) {
			logger.debug("cleaning up the application data " + this);
		}
		initialPoppedRoute = null;
		proxyBranch = null;
		// cannot nullify at the same time because of noAckReceived needs it and TCK SipApplicationSessionListenerTest
//		if(cleanUpSipServletMessage && sipServletMessage != null) {
//			sipServletMessage.cleanUp();
//			if(sipServletMessage instanceof SipServletRequestImpl) {
//				((SipServletRequestImpl)sipServletMessage).cleanUpLastResponses();
//			}
//			sipSessionKey = sipServletMessage.getSipSessionKey();
//			sipServletMessage = null;
//		}
		if(sipServletResponses != null) {
			sipServletResponses.clear();
			sipServletResponses = null;
		}
		transaction = null;
		rseqNumber = null;
		if(hops != null
		        // https://code.google.com/p/sipservlets/issues/detail?id=249
		        && hops.size() <= 1) {
			hops.clear();
			hops = null;
		    if(logger.isDebugEnabled()) {
	            logger.debug("cleaned up tx app data hops");
	        }
		}
	}

	public void cleanUpMessage() {
		// https://github.com/RestComm/sip-servlets/issues/107
		// this one may need to be synchronized as it can be called 
		// for cleanup from multiple places and may result in 
		// sipservletmessage being null sometimes and throwing NPE
		if(messageCleanedUp.compareAndSet(false, true)) {
			if(sipServletMessage != null) {
				sipSessionKey = sipServletMessage.getSipSessionKey();
				method = sipServletMessage.getMethod();
				if(logger.isDebugEnabled()) {
					logger.debug("cleaning up the application data " + this + " from the sipservletmessage " + sipServletMessage);
				}
				sipServletMessage.cleanUp();
				if(sipServletMessage instanceof SipServletRequestImpl) {
					((SipServletRequestImpl)sipServletMessage).cleanUpLastResponses();
				}
				sipServletMessage = null;
			}
		}
	}
	
	/**
	 * @param hops the hops to set
	 */
	public void setHops(Queue<Hop> hops) {
		this.hops = hops;
	}

	/**
	 * @return the hops
	 */
	public Queue<Hop> getHops() {
		return hops;
	}

//	public void readExternal(ObjectInput in) throws IOException,
//			ClassNotFoundException {
//		sipServletMessage = (SipServletMessageImpl) in.readObject();
//		if(((ClusteredSipStack)StaticServiceHolder.sipStandardService.getSipStack()).getReplicationStrategy() == ReplicationStrategy.EarlyDialog) {
//			boolean proxyBranchSerialized = in.readBoolean();
//			if(proxyBranchSerialized) {
//				proxyBranch = (ProxyBranchImpl) in.readObject();
//			}
//			int size = in.readInt();
//			if(size > 0) {
//				SipServletResponseImpl[] sipServletResponseImpls = (SipServletResponseImpl[])in.readObject();
//				for (SipServletResponseImpl sipServletResponseImpl : sipServletResponseImpls) {
//					addSipServletResponse(sipServletResponseImpl);
//				}
//			}
//		}
//	}
//
//	public void writeExternal(ObjectOutput out) throws IOException {
//		out.writeObject(sipServletMessage);
//		if(((ClusteredSipStack)StaticServiceHolder.sipStandardService.getSipStack()).getReplicationStrategy() == ReplicationStrategy.EarlyDialog) {
//			if(proxyBranch != null) {
//				out.writeBoolean(true);
//				out.writeObject(proxyBranch);
//			} else {
//				out.writeBoolean(false);
//			}
//			if(sipServletResponses != null) {
//				out.writeInt(sipServletResponses.size());
//				out.writeObject(sipServletResponses.toArray(new SipServletResponseImpl[sipServletResponses.size()]));
//			} else {
//				out.writeInt(0);
//			}
//		}
//	}

	/**
	 * @return the sipSessionKey
	 */
	public MobicentsSipSessionKey getSipSessionKey() {
		if(logger.isDebugEnabled()) {
			logger.debug("local session Key is " + sipSessionKey);
		}
		if(sipSessionKey == null && sipServletMessage != null) {
			MobicentsSipSessionKey sessionKey = sipServletMessage.getSipSessionKey();
			if(logger.isDebugEnabled()) {
				logger.debug("session Key from sipservletmessage is " + sessionKey);
			}
			sipSessionKey = sessionKey;
		}
		return sipSessionKey;
	}	
	
	/**
	 * @return the sipSessionKey
	 */
	public String getMethod() {
		if(sipServletMessage != null) {
			return sipServletMessage.getMethod();
		}
		return method;
	}	
}
