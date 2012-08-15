/*
 * TeleStax, Open Source Cloud Communications  Copyright 2012.
 * and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.protocols.ss7.map.service.mobility;

import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.ss7.map.MAPDialogImpl;
import org.mobicents.protocols.ss7.map.MAPProviderImpl;
import org.mobicents.protocols.ss7.map.api.MAPApplicationContext;
import org.mobicents.protocols.ss7.map.api.MAPApplicationContextName;
import org.mobicents.protocols.ss7.map.api.MAPApplicationContextVersion;
import org.mobicents.protocols.ss7.map.api.MAPException;
import org.mobicents.protocols.ss7.map.api.MAPOperationCode;
import org.mobicents.protocols.ss7.map.api.primitives.AddressString;
import org.mobicents.protocols.ss7.map.api.primitives.GSNAddress;
import org.mobicents.protocols.ss7.map.api.primitives.IMEI;
import org.mobicents.protocols.ss7.map.api.primitives.IMSI;
import org.mobicents.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.mobicents.protocols.ss7.map.api.primitives.LMSI;
import org.mobicents.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.mobicents.protocols.ss7.map.api.primitives.PlmnId;
import org.mobicents.protocols.ss7.map.api.primitives.SubscriberIdentity;
import org.mobicents.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.mobicents.protocols.ss7.map.api.service.mobility.MAPServiceMobility;
import org.mobicents.protocols.ss7.map.api.service.mobility.authentication.AuthenticationSetList;
import org.mobicents.protocols.ss7.map.api.service.mobility.authentication.EpsAuthenticationSetList;
import org.mobicents.protocols.ss7.map.api.service.mobility.authentication.ReSynchronisationInfo;
import org.mobicents.protocols.ss7.map.api.service.mobility.authentication.RequestingNodeType;
import org.mobicents.protocols.ss7.map.api.service.mobility.imei.EquipmentStatus;
import org.mobicents.protocols.ss7.map.api.service.mobility.imei.RequestedEquipmentInfo;
import org.mobicents.protocols.ss7.map.api.service.mobility.imei.UESBIIu;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.ADDInfo;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.PagingArea;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.VLRCapability;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.RequestedInfo;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberInfo;
import org.mobicents.protocols.ss7.map.service.mobility.authentication.SendAuthenticationInfoRequestImpl;
import org.mobicents.protocols.ss7.map.service.mobility.authentication.SendAuthenticationInfoResponseImpl;
import org.mobicents.protocols.ss7.map.service.mobility.imei.CheckImeiRequestImpl;
import org.mobicents.protocols.ss7.map.service.mobility.imei.CheckImeiResponseImpl;
import org.mobicents.protocols.ss7.map.service.mobility.locationManagement.UpdateLocationRequestImpl;
import org.mobicents.protocols.ss7.map.service.mobility.locationManagement.UpdateLocationResponseImpl;
import org.mobicents.protocols.ss7.map.service.mobility.subscriberInformation.AnyTimeInterrogationRequestImpl;
import org.mobicents.protocols.ss7.map.service.mobility.subscriberInformation.AnyTimeInterrogationResponseImpl;
import org.mobicents.protocols.ss7.tcap.api.TCAPException;
import org.mobicents.protocols.ss7.tcap.api.tc.dialog.Dialog;
import org.mobicents.protocols.ss7.tcap.asn.TcapFactory;
import org.mobicents.protocols.ss7.tcap.asn.comp.Invoke;
import org.mobicents.protocols.ss7.tcap.asn.comp.OperationCode;
import org.mobicents.protocols.ss7.tcap.asn.comp.Parameter;
import org.mobicents.protocols.ss7.tcap.asn.comp.ReturnResultLast;

/**
 * 
 * @author sergey vetyutnev
 * 
 */
public class MAPDialogMobilityImpl extends MAPDialogImpl implements MAPDialogMobility {

	protected MAPDialogMobilityImpl(MAPApplicationContext appCntx, Dialog tcapDialog, MAPProviderImpl mapProviderImpl, MAPServiceMobility mapService,
			AddressString origReference, AddressString destReference) {
		super(appCntx, tcapDialog, mapProviderImpl, mapService, origReference, destReference);
	}

	
	public Long addSendAuthenticationInfoRequest(IMSI imsi, int numberOfRequestedVectors, boolean segmentationProhibited, boolean immediateResponsePreferred,
			ReSynchronisationInfo reSynchronisationInfo, MAPExtensionContainer extensionContainer, RequestingNodeType requestingNodeType,
			PlmnId requestingPlmnId, Integer numberOfRequestedAdditionalVectors, boolean additionalVectorsAreForEPS) throws MAPException {
		return this
				.addSendAuthenticationInfoRequest(_Timer_Default, imsi, numberOfRequestedVectors, segmentationProhibited, immediateResponsePreferred,
						reSynchronisationInfo, extensionContainer, requestingNodeType, requestingPlmnId, numberOfRequestedAdditionalVectors,
						additionalVectorsAreForEPS);
	}

	public Long addSendAuthenticationInfoRequest(int customInvokeTimeout, IMSI imsi, int numberOfRequestedVectors, boolean segmentationProhibited,
			boolean immediateResponsePreferred, ReSynchronisationInfo reSynchronisationInfo, MAPExtensionContainer extensionContainer,
			RequestingNodeType requestingNodeType, PlmnId requestingPlmnId, Integer numberOfRequestedAdditionalVectors, boolean additionalVectorsAreForEPS)
			throws MAPException {

		if ((this.appCntx.getApplicationContextName() != MAPApplicationContextName.infoRetrievalContext)
				|| (this.appCntx.getApplicationContextVersion() != MAPApplicationContextVersion.version2 && this.appCntx.getApplicationContextVersion() != MAPApplicationContextVersion.version3))
			throw new MAPException("Bad application context name for sendAuthenticationInfoRequest: must be infoRetrievalContext_V2 or V3");

		Invoke invoke = this.mapProviderImpl.getTCAPProvider().getComponentPrimitiveFactory().createTCInvokeRequest();
		if (customInvokeTimeout == _Timer_Default)
			invoke.setTimeout(_Timer_m);
		else
			invoke.setTimeout(customInvokeTimeout);

		OperationCode oc = this.mapProviderImpl.getTCAPProvider().getComponentPrimitiveFactory().createOperationCode();
		oc.setLocalOperationCode((long) MAPOperationCode.sendAuthenticationInfo);
		invoke.setOperationCode(oc);

		if (imsi != null) {
			// parameter is optional: is no imsi is included we will not add a parameter 
			SendAuthenticationInfoRequestImpl req = new SendAuthenticationInfoRequestImpl(this.appCntx.getApplicationContextVersion().getVersion(), imsi,
					numberOfRequestedVectors, segmentationProhibited, immediateResponsePreferred, reSynchronisationInfo, extensionContainer,
					requestingNodeType, requestingPlmnId, numberOfRequestedAdditionalVectors, additionalVectorsAreForEPS);
			AsnOutputStream aos = new AsnOutputStream();
			req.encodeData(aos);

			Parameter p = this.mapProviderImpl.getTCAPProvider().getComponentPrimitiveFactory().createParameter();
			p.setTagClass(req.getTagClass());
			p.setPrimitive(req.getIsPrimitive());
			p.setTag(req.getTag());
			p.setData(aos.toByteArray());
			invoke.setParameter(p);
		}		

		Long invokeId;
		try {
			invokeId = this.tcapDialog.getNewInvokeId();
			invoke.setInvokeId(invokeId);
		} catch (TCAPException e) {
			throw new MAPException(e.getMessage(), e);
		}

		this.sendInvokeComponent(invoke);

		return invokeId;
	}

	public void addSendAuthenticationInfoResponse(long invokeId, AuthenticationSetList authenticationSetList, MAPExtensionContainer extensionContainer,
			EpsAuthenticationSetList epsAuthenticationSetList) throws MAPException {

		if ((this.appCntx.getApplicationContextName() != MAPApplicationContextName.infoRetrievalContext)
				|| (this.appCntx.getApplicationContextVersion() != MAPApplicationContextVersion.version2 && this.appCntx.getApplicationContextVersion() != MAPApplicationContextVersion.version3))
			throw new MAPException("Bad application context name for addSendAuthenticationInfoResponse: must be infoRetrievalContext_V2 or V3");

		ReturnResultLast resultLast = this.mapProviderImpl.getTCAPProvider().getComponentPrimitiveFactory().createTCResultLastRequest();

		resultLast.setInvokeId(invokeId);

		if (authenticationSetList != null || extensionContainer != null || epsAuthenticationSetList != null) {
			// Operation Code
			OperationCode oc = this.mapProviderImpl.getTCAPProvider().getComponentPrimitiveFactory().createOperationCode();
			oc.setLocalOperationCode((long) MAPOperationCode.sendAuthenticationInfo);
			resultLast.setOperationCode(oc);

			SendAuthenticationInfoResponseImpl req = new SendAuthenticationInfoResponseImpl(this.appCntx.getApplicationContextVersion().getVersion(),
					authenticationSetList, extensionContainer, epsAuthenticationSetList);
			AsnOutputStream aos = new AsnOutputStream();
			req.encodeData(aos);

			Parameter p = this.mapProviderImpl.getTCAPProvider().getComponentPrimitiveFactory().createParameter();
			p.setTagClass(req.getTagClass());
			p.setPrimitive(req.getIsPrimitive());
			p.setTag(req.getTag());
			p.setData(aos.toByteArray());
			resultLast.setParameter(p);
		}

		this.sendReturnResultLastComponent(resultLast);
	}

	
	public Long addUpdateLocationRequest(IMSI imsi, ISDNAddressString mscNumber, ISDNAddressString roamingNumber, ISDNAddressString vlrNumber, LMSI lmsi,
			MAPExtensionContainer extensionContainer, VLRCapability vlrCapability, boolean informPreviousNetworkEntity, boolean csLCSNotSupportedByUE,
			GSNAddress vGmlcAddress, ADDInfo addInfo, PagingArea pagingArea, boolean skipSubscriberDataUpdate, boolean restorationIndicator)
			throws MAPException {
		return addUpdateLocationRequest(_Timer_Default, imsi, mscNumber, roamingNumber, vlrNumber, lmsi, extensionContainer, vlrCapability,
				informPreviousNetworkEntity, csLCSNotSupportedByUE, vGmlcAddress, addInfo, pagingArea, skipSubscriberDataUpdate, restorationIndicator);
	}

	public Long addUpdateLocationRequest(int customInvokeTimeout, IMSI imsi, ISDNAddressString mscNumber, ISDNAddressString roamingNumber,
			ISDNAddressString vlrNumber, LMSI lmsi, MAPExtensionContainer extensionContainer, VLRCapability vlrCapability, boolean informPreviousNetworkEntity,
			boolean csLCSNotSupportedByUE, GSNAddress vGmlcAddress, ADDInfo addInfo, PagingArea pagingArea, boolean skipSubscriberDataUpdate,
			boolean restorationIndicator) throws MAPException {

		if ((this.appCntx.getApplicationContextName() != MAPApplicationContextName.networkLocUpContext)
				|| (this.appCntx.getApplicationContextVersion() != MAPApplicationContextVersion.version1
						&& this.appCntx.getApplicationContextVersion() != MAPApplicationContextVersion.version2 && 
						this.appCntx.getApplicationContextVersion() != MAPApplicationContextVersion.version3))
			throw new MAPException("Bad application context name for UpdateLocationRequest: must be networkLocUpContext_V1, V2 or V3");

		Invoke invoke = this.mapProviderImpl.getTCAPProvider().getComponentPrimitiveFactory().createTCInvokeRequest();
		if (customInvokeTimeout == _Timer_Default)
			invoke.setTimeout(_Timer_m);
		else
			invoke.setTimeout(customInvokeTimeout);

		OperationCode oc = this.mapProviderImpl.getTCAPProvider().getComponentPrimitiveFactory().createOperationCode();
		oc.setLocalOperationCode((long) MAPOperationCode.updateLocation);
		invoke.setOperationCode(oc);

		UpdateLocationRequestImpl req = new UpdateLocationRequestImpl(this.appCntx.getApplicationContextVersion().getVersion(), imsi, mscNumber, roamingNumber,
				vlrNumber, lmsi, extensionContainer, vlrCapability, informPreviousNetworkEntity, csLCSNotSupportedByUE, vGmlcAddress, addInfo, pagingArea,
				skipSubscriberDataUpdate, restorationIndicator);
		AsnOutputStream aos = new AsnOutputStream();
		req.encodeData(aos);

		Parameter p = this.mapProviderImpl.getTCAPProvider().getComponentPrimitiveFactory().createParameter();
		p.setTagClass(req.getTagClass());
		p.setPrimitive(req.getIsPrimitive());
		p.setTag(req.getTag());
		p.setData(aos.toByteArray());
		invoke.setParameter(p);

		Long invokeId;
		try {
			invokeId = this.tcapDialog.getNewInvokeId();
			invoke.setInvokeId(invokeId);
		} catch (TCAPException e) {
			throw new MAPException(e.getMessage(), e);
		}

		this.sendInvokeComponent(invoke);

		return invokeId;
	}

	public void addUpdateLocationResponse(long invokeId, ISDNAddressString hlrNumber, MAPExtensionContainer extensionContainer,
			boolean addCapability, boolean pagingAreaCapability) throws MAPException {

		if ((this.appCntx.getApplicationContextName() != MAPApplicationContextName.networkLocUpContext)
				|| (this.appCntx.getApplicationContextVersion() != MAPApplicationContextVersion.version1
						&& this.appCntx.getApplicationContextVersion() != MAPApplicationContextVersion.version2 && 
						this.appCntx.getApplicationContextVersion() != MAPApplicationContextVersion.version3))
			throw new MAPException("Bad application context name for UpdateLocationResponse: must be networkLocUpContext_V1, V2 or V3");

		ReturnResultLast resultLast = this.mapProviderImpl.getTCAPProvider().getComponentPrimitiveFactory().createTCResultLastRequest();

		resultLast.setInvokeId(invokeId);

		// Operation Code
		OperationCode oc = this.mapProviderImpl.getTCAPProvider().getComponentPrimitiveFactory().createOperationCode();
		oc.setLocalOperationCode((long) MAPOperationCode.updateLocation);
		resultLast.setOperationCode(oc);

		UpdateLocationResponseImpl req = new UpdateLocationResponseImpl(this.appCntx.getApplicationContextVersion().getVersion(), hlrNumber,
				extensionContainer, addCapability, pagingAreaCapability);
		AsnOutputStream aos = new AsnOutputStream();
		req.encodeData(aos);

		Parameter p = this.mapProviderImpl.getTCAPProvider().getComponentPrimitiveFactory().createParameter();
		p.setTagClass(req.getTagClass());
		p.setPrimitive(req.getIsPrimitive());
		p.setTag(req.getTag());
		p.setData(aos.toByteArray());
		resultLast.setParameter(p);

		this.sendReturnResultLastComponent(resultLast);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.protocols.ss7.map.api.service.subscriberInformation.
	 * MAPDialogSubscriberInformation
	 * #addAnyTimeInterrogationRequest(org.mobicents
	 * .protocols.ss7.map.api.primitives.SubscriberIdentity,
	 * org.mobicents.protocols
	 * .ss7.map.api.service.subscriberInformation.RequestedInfo,
	 * org.mobicents.protocols.ss7.map.api.primitives.ISDNAddressString,
	 * org.mobicents.protocols.ss7.map.api.primitives.MAPExtensionContainer)
	 */
	public long addAnyTimeInterrogationRequest(SubscriberIdentity subscriberIdentity, RequestedInfo requestedInfo, ISDNAddressString gsmSCFAddress,
			MAPExtensionContainer extensionContainer) throws MAPException {

		return this.addAnyTimeInterrogationRequest(_Timer_Default, subscriberIdentity, requestedInfo, gsmSCFAddress, extensionContainer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.protocols.ss7.map.api.service.subscriberInformation.
	 * MAPDialogSubscriberInformation#addAnyTimeInterrogationRequest(long,
	 * org.mobicents.protocols.ss7.map.api.primitives.SubscriberIdentity,
	 * org.mobicents
	 * .protocols.ss7.map.api.service.subscriberInformation.RequestedInfo,
	 * org.mobicents.protocols.ss7.map.api.primitives.ISDNAddressString,
	 * org.mobicents.protocols.ss7.map.api.primitives.MAPExtensionContainer)
	 */
	public long addAnyTimeInterrogationRequest(long customInvokeTimeout, SubscriberIdentity subscriberIdentity, RequestedInfo requestedInfo,
			ISDNAddressString gsmSCFAddress, MAPExtensionContainer extensionContainer) throws MAPException {

		if ((this.appCntx.getApplicationContextName() != MAPApplicationContextName.anyTimeEnquiryContext)
				|| (this.appCntx.getApplicationContextVersion() != MAPApplicationContextVersion.version3))
			throw new MAPException("Bad application context name for AnyTimeInterrogationRequest: must be networkLocUpContext_V3");

		Invoke invoke = this.mapProviderImpl.getTCAPProvider().getComponentPrimitiveFactory().createTCInvokeRequest();
		if (customInvokeTimeout == _Timer_Default)
			invoke.setTimeout(_Timer_m);
		else
			invoke.setTimeout(customInvokeTimeout);

		// Operation Code
		OperationCode oc = TcapFactory.createOperationCode();
		oc.setLocalOperationCode((long) MAPOperationCode.anyTimeInterrogation);
		invoke.setOperationCode(oc);

		AnyTimeInterrogationRequestImpl req = new AnyTimeInterrogationRequestImpl(subscriberIdentity, requestedInfo, gsmSCFAddress,
				extensionContainer);

		AsnOutputStream aos = new AsnOutputStream();
		req.encodeData(aos);

		Parameter p = this.mapProviderImpl.getTCAPProvider().getComponentPrimitiveFactory().createParameter();
		p.setTagClass(req.getTagClass());
		p.setPrimitive(req.getIsPrimitive());
		p.setTag(req.getTag());
		p.setData(aos.toByteArray());
		invoke.setParameter(p);

		Long invokeId;
		try {
			invokeId = this.tcapDialog.getNewInvokeId();
			invoke.setInvokeId(invokeId);
		} catch (TCAPException e) {
			throw new MAPException(e.getMessage(), e);
		}

		this.sendInvokeComponent(invoke);

		return invokeId;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.protocols.ss7.map.api.service.subscriberInformation.
	 * MAPDialogSubscriberInformation#addAnyTimeInterrogationResponse(long)
	 */
	public void addAnyTimeInterrogationResponse(long invokeId, SubscriberInfo subscriberInfo, MAPExtensionContainer extensionContainer) throws MAPException {

		if ((this.appCntx.getApplicationContextName() != MAPApplicationContextName.anyTimeEnquiryContext)
				|| (this.appCntx.getApplicationContextVersion() != MAPApplicationContextVersion.version3))
			throw new MAPException("Bad application context name for AnyTimeInterrogationRequest: must be networkLocUpContext_V3");

		ReturnResultLast resultLast = this.mapProviderImpl.getTCAPProvider().getComponentPrimitiveFactory().createTCResultLastRequest();

		resultLast.setInvokeId(invokeId);

		// Operation Code
		OperationCode oc = this.mapProviderImpl.getTCAPProvider().getComponentPrimitiveFactory().createOperationCode();
		oc.setLocalOperationCode((long) MAPOperationCode.anyTimeInterrogation);
		resultLast.setOperationCode(oc);

		AnyTimeInterrogationResponseImpl req = new AnyTimeInterrogationResponseImpl(subscriberInfo, extensionContainer);
		AsnOutputStream aos = new AsnOutputStream();
		req.encodeData(aos);

		Parameter p = this.mapProviderImpl.getTCAPProvider().getComponentPrimitiveFactory().createParameter();
		p.setTagClass(req.getTagClass());
		p.setPrimitive(req.getIsPrimitive());
		p.setTag(req.getTag());
		p.setData(aos.toByteArray());
		resultLast.setParameter(p);

		this.sendReturnResultLastComponent(resultLast);
	}

	@Override
	public Long addCheckImeiRequest(IMEI imei) throws MAPException {
		return this.addCheckImeiRequest(_Timer_Default, imei, null, null);
	}
	
	@Override
	public Long addCheckImeiRequest(long customInvokeTimeout, IMEI imei) throws MAPException {
		return this.addCheckImeiRequest(customInvokeTimeout, imei, null, null);
	}
	
	@Override
	public Long addCheckImeiRequest(IMEI imei, RequestedEquipmentInfo requestedEquipmentInfo, 
			MAPExtensionContainer extensionContainer) throws MAPException {
		
		return this.addCheckImeiRequest(_Timer_Default, imei, requestedEquipmentInfo, extensionContainer);
	}
	
	@Override
	public Long addCheckImeiRequest(long customInvokeTimeout, IMEI imei, RequestedEquipmentInfo requestedEquipmentInfo,
			MAPExtensionContainer extensionContainer) throws MAPException {

		if ((this.appCntx.getApplicationContextName() != MAPApplicationContextName.equipmentMngtContext)
				|| (this.appCntx.getApplicationContextVersion() != MAPApplicationContextVersion.version1
						&& this.appCntx.getApplicationContextVersion() != MAPApplicationContextVersion.version2
						&& this.appCntx.getApplicationContextVersion() != MAPApplicationContextVersion.version3)) {
			throw new MAPException("Bad application context name for CheckImeiRequest: must be equipmentMngtContext_V1, V2 or V3");
		}
		
		Invoke invoke = this.mapProviderImpl.getTCAPProvider().getComponentPrimitiveFactory().createTCInvokeRequest();
		if (customInvokeTimeout == _Timer_Default) {
			invoke.setTimeout(_Timer_m);
		}
		else {
			invoke.setTimeout(customInvokeTimeout);
		}

		// Operation Code
		OperationCode oc = TcapFactory.createOperationCode();
		oc.setLocalOperationCode((long) MAPOperationCode.checkIMEI);
		invoke.setOperationCode(oc);
		
		CheckImeiRequestImpl req = new CheckImeiRequestImpl(this.appCntx.getApplicationContextVersion().getVersion()
				, imei, requestedEquipmentInfo,	extensionContainer);

		AsnOutputStream aos = new AsnOutputStream();
		req.encodeData(aos);

		Parameter p = this.mapProviderImpl.getTCAPProvider().getComponentPrimitiveFactory().createParameter();
		p.setTagClass(req.getTagClass());
		p.setPrimitive(req.getIsPrimitive());
		p.setTag(req.getTag());
		p.setData(aos.toByteArray());
		invoke.setParameter(p);
		
		Long invokeId;
		try {
			invokeId = this.tcapDialog.getNewInvokeId();
			invoke.setInvokeId(invokeId);
		} catch (TCAPException e) {
			throw new MAPException(e.getMessage(), e);
		}

		this.sendInvokeComponent(invoke);

		return invokeId;
	}
	
	@Override
	public void addCheckImeiResponse(long invokeId, EquipmentStatus equipmentStatus) throws MAPException {
		this.addCheckImeiResponse(invokeId, equipmentStatus, null, null);
	}
	
	@Override
	public void addCheckImeiResponse(long invokeId, EquipmentStatus equipmentStatus, UESBIIu bmuef, MAPExtensionContainer extensionContainer) throws MAPException {
		if ((this.appCntx.getApplicationContextName() != MAPApplicationContextName.equipmentMngtContext)
				|| (this.appCntx.getApplicationContextVersion() != MAPApplicationContextVersion.version1
						&& this.appCntx.getApplicationContextVersion() != MAPApplicationContextVersion.version2
						&& this.appCntx.getApplicationContextVersion() != MAPApplicationContextVersion.version3)) {
			throw new MAPException("Bad application context name for CheckImeiResponse: must be equipmentMngtContext_V1, V2 or V3");
		}
		
		ReturnResultLast resultLast = this.mapProviderImpl.getTCAPProvider().getComponentPrimitiveFactory().createTCResultLastRequest();
		resultLast.setInvokeId(invokeId);
		
		// Operation Code
		OperationCode oc = this.mapProviderImpl.getTCAPProvider().getComponentPrimitiveFactory().createOperationCode();
		oc.setLocalOperationCode((long) MAPOperationCode.checkIMEI);
		resultLast.setOperationCode(oc);
		
		CheckImeiResponseImpl resp = new CheckImeiResponseImpl(this.appCntx.getApplicationContextVersion().getVersion(), equipmentStatus, bmuef, extensionContainer);
		AsnOutputStream aos = new AsnOutputStream();
		resp.encodeData(aos);
		
		Parameter p = this.mapProviderImpl.getTCAPProvider().getComponentPrimitiveFactory().createParameter();
		p.setTagClass(resp.getTagClass());
		p.setPrimitive(resp.getIsPrimitive());
		p.setTag(resp.getTag());
		p.setData(aos.toByteArray());
		resultLast.setParameter(p);

		this.sendReturnResultLastComponent(resultLast);
	}

}

