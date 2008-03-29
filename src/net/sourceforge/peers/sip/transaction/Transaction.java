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

package net.sourceforge.peers.sip.transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.peers.sip.transport.SipRequest;
import net.sourceforge.peers.sip.transport.SipResponse;


public abstract class Transaction {

    public static final char ID_SEPARATOR = '|';
    
    protected String branchId;
    protected String method;
    
    protected SipRequest request;
    protected List<SipResponse> responses;

    protected Transaction(String branchId, String method) {
        super();
        this.branchId = branchId;
        this.method = method;
        responses = Collections.synchronizedList(new ArrayList<SipResponse>());
    }

    protected String getId() {
        StringBuffer buf = new StringBuffer();
        buf.append(branchId).append(ID_SEPARATOR);
        buf.append(method);
        return buf.toString();
    }

    public SipResponse getLastResponse() {
        return responses.get(responses.size() - 1);
    }

    public SipRequest getRequest() {
        return request;
    }
    
}