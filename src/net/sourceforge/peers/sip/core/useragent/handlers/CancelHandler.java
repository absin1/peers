/*
    This file is part of Peers.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
    Copyright 2007, 2008 Yohann Martineau 
*/

package net.sourceforge.peers.sip.core.useragent.handlers;

import net.sourceforge.peers.sip.RFC3261;
import net.sourceforge.peers.sip.Utils;
import net.sourceforge.peers.sip.core.useragent.UAC;
import net.sourceforge.peers.sip.syntaxencoding.SipHeaderFieldName;
import net.sourceforge.peers.sip.syntaxencoding.SipHeaderFieldValue;
import net.sourceforge.peers.sip.syntaxencoding.SipHeaderParamName;
import net.sourceforge.peers.sip.syntaxencoding.SipHeaders;
import net.sourceforge.peers.sip.transaction.ClientTransaction;
import net.sourceforge.peers.sip.transaction.InviteClientTransaction;
import net.sourceforge.peers.sip.transaction.InviteServerTransaction;
import net.sourceforge.peers.sip.transaction.ServerTransaction;
import net.sourceforge.peers.sip.transaction.ServerTransactionUser;
import net.sourceforge.peers.sip.transaction.TransactionManager;
import net.sourceforge.peers.sip.transactionuser.Dialog;
import net.sourceforge.peers.sip.transactionuser.DialogManager;
import net.sourceforge.peers.sip.transport.SipRequest;
import net.sourceforge.peers.sip.transport.SipResponse;

public class CancelHandler extends MethodHandler implements ServerTransactionUser {

    //////////////////////////////////////////////////////////
    // UAS methods
    //////////////////////////////////////////////////////////
    
    public void handleCancel(SipRequest sipRequest) {
        SipHeaderFieldValue topVia = Utils.getInstance().getTopVia(sipRequest);
        String branchId = topVia.getParam(new SipHeaderParamName(
                RFC3261.PARAM_BRANCH));
        InviteServerTransaction inviteServerTransaction =
            (InviteServerTransaction)TransactionManager.getInstance()
                .getServerTransaction(branchId,RFC3261.METHOD_INVITE);
        SipResponse cancelResponse;
        if (inviteServerTransaction == null) {
            //TODO generate CANCEL 481 Call Leg/Transaction Does Not Exist
            cancelResponse = buildGenericResponse(sipRequest,
                    RFC3261.CODE_481_CALL_TRANSACTION_DOES_NOT_EXIST,
                    RFC3261.REASON_481_CALL_TRANSACTION_DOES_NOT_EXIST);
        } else {
            cancelResponse = buildGenericResponse(sipRequest,
                    RFC3261.CODE_200_OK, RFC3261.REASON_200_OK);
        }
        ServerTransaction cancelServerTransaction = TransactionManager.getInstance()
                .createServerTransaction(cancelResponse,
                        Utils.getInstance().getSipPort(),
                        RFC3261.TRANSPORT_UDP, this, sipRequest);
        cancelServerTransaction.start();
        cancelServerTransaction.receivedRequest(sipRequest);
        cancelServerTransaction.sendReponse(cancelResponse);
        if (cancelResponse.getStatusCode() != RFC3261.CODE_200_OK) {
            return;
        }
        
        SipResponse lastResponse = inviteServerTransaction.getLastResponse();
        if (lastResponse != null &&
                lastResponse.getStatusCode() >= RFC3261.CODE_200_OK) {
            return;
        }
        
        SipResponse inviteResponse = buildGenericResponse(
                inviteServerTransaction.getRequest(),
                RFC3261.CODE_487_REQUEST_TERMINATED,
                RFC3261.REASON_487_REQUEST_TERMINATED);
        inviteServerTransaction.sendReponse(inviteResponse);
        
        Dialog dialog = DialogManager.getInstance().getDialog(lastResponse);
        dialog.receivedOrSent300To699();
        
    }
    
    //////////////////////////////////////////////////////////
    // UAC methods
    //////////////////////////////////////////////////////////
    
    public ClientTransaction preProcessCancel(SipRequest cancelGenericRequest,
            SipRequest inviteRequest) {
        //TODO
        //p. 54 §9.1
        
        SipHeaders cancelHeaders = cancelGenericRequest.getSipHeaders();
        SipHeaders inviteHeaders = inviteRequest.getSipHeaders();
        
        //cseq
        SipHeaderFieldName cseqName = new SipHeaderFieldName(RFC3261.HDR_CSEQ);
        SipHeaderFieldValue cancelCseq = cancelHeaders.get(cseqName);
        SipHeaderFieldValue inviteCseq = inviteHeaders.get(cseqName);
        cancelCseq.setValue(inviteCseq.getValue().replace(RFC3261.METHOD_INVITE,
                RFC3261.METHOD_CANCEL));

        
        //from
        SipHeaderFieldName fromName = new SipHeaderFieldName(RFC3261.HDR_FROM);
        SipHeaderFieldValue cancelFrom = cancelHeaders.get(fromName);
        SipHeaderFieldValue inviteFrom = inviteHeaders.get(fromName);
        cancelFrom.setValue(inviteFrom.getValue());
        
        //top-via
//        cancelHeaders.add(new SipHeaderFieldName(RFC3261.HDR_VIA),
//                Utils.getInstance().getTopVia(inviteRequest));
        SipHeaderFieldValue topVia = Utils.getInstance().getTopVia(inviteRequest);
        String branchId = topVia.getParam(new SipHeaderParamName(RFC3261.PARAM_BRANCH));
        
        //route
        SipHeaderFieldName routeName = new SipHeaderFieldName(RFC3261.HDR_ROUTE);
        SipHeaderFieldValue inviteRoute = inviteHeaders.get(routeName);
        if (inviteRoute != null) {
            cancelHeaders.add(routeName, inviteRoute);
        }
        
        
        InviteClientTransaction inviteClientTransaction = (InviteClientTransaction)
        TransactionManager.getInstance().getClientTransaction(inviteRequest);
        SipResponse lastResponse = inviteClientTransaction.getLastResponse();
        if (lastResponse.getStatusCode() >= RFC3261.CODE_200_OK) {
            return null;
        }

        
        return UAC.getInstance().getMidDialogRequestManager()
            .createNonInviteClientTransaction(cancelGenericRequest, branchId);
    }

    public void transactionFailure() {
        // TODO Auto-generated method stub
        
    }
}