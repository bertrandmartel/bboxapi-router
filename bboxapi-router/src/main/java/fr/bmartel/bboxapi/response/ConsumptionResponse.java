/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2017 Bertrand Martel
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fr.bmartel.bboxapi.response;

import fr.bmartel.bboxapi.model.HttpStatus;
import fr.bmartel.bboxapi.model.profile.ProfileEntry;
import fr.bmartel.bboxapi.model.voip.VoipEntry;
import org.apache.http.StatusLine;

import java.util.List;

/**
 * Response to profile consumption request.
 *
 * @author Bertrand Martel
 */
public class ConsumptionResponse extends HttpResponse {

    private List<ProfileEntry> mProfileList;

    private boolean mValidSession;

    public ConsumptionResponse(List<ProfileEntry> profileList, HttpStatus status, StatusLine statusLine) {
        super(status, statusLine);
        mProfileList = profileList;
    }

    public List<ProfileEntry> getProfileList() {
        return mProfileList;
    }

    public boolean isValidSession() {
        return mValidSession;
    }

    public void setValidSession(boolean validSession) {
        mValidSession = validSession;
    }
}
