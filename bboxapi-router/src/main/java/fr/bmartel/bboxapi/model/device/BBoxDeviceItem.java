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
package fr.bmartel.bboxapi.model.device;

import com.google.gson.annotations.SerializedName;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Bbox device info.
 *
 * @author Bertrand Martel
 */
public class BBoxDeviceItem {

    @SerializedName("now")
    private String mNow;

    /**
     * device status ?
     */
    @SerializedName("status")
    private int mStatus;

    /**
     * number of boot
     */
    @SerializedName("numberofboots")
    private int mBootNumber;

    /**
     * box model name
     */
    @SerializedName("modelname")
    private String mModelName;

    /**
     * define if user has already logged before
     */
    @SerializedName("user_configured")
    private int mUserConfigured;

    @SerializedName("display")
    private Display mDisplay;

    @SerializedName("main")
    private Versionning mMain;

    @SerializedName("reco")
    private Versionning mReco;

    @SerializedName("bcck")
    private Versionning mBcck;

    @SerializedName("ldr1")
    private Versionning mLdr1;

    @SerializedName("ldr2")
    private Versionning mLdr2;

    @SerializedName("firstusedate")
    private String mFirstUseDate;

    @SerializedName("uptime")
    private int mUptime;

    /**
     * bbox serial number
     */
    @SerializedName("serialnumber")
    private String mSerialNumber = "";

    public String getSerialNumber() {
        return mSerialNumber;
    }

    public int getStatus() {
        return mStatus;
    }

    public int getBootNumber() {
        return mBootNumber;
    }

    public String getModelName() {
        return mModelName;
    }

    public int isUserConfigured() {
        return mUserConfigured;
    }

    public Display getDisplay() {
        return mDisplay;
    }

    public String getFirstUseDate() {
        return mFirstUseDate;
    }

    public int getUptime() {
        return mUptime;
    }

    public String getNow() {
        return mNow;
    }

    public Versionning getMain() {
        return mMain;
    }

    public Versionning getReco() {
        return mReco;
    }

    public Versionning getBcck() {
        return mBcck;
    }

    public Versionning getLdr1() {
        return mLdr1;
    }

    public Versionning getLdr2() {
        return mLdr2;
    }

    private static int getVersionPattern(String input, int index) {
        Matcher match = Pattern.compile("(\\d+).(\\d+).(\\d+)").matcher(input);
        return match.matches() ? Integer.parseInt(match.group(index)) : -1;
    }

    public class Versionning {

        @SerializedName("version")
        private String mVersion;

        @SerializedName("date")
        private String mDate;

        public String getVersion() {
            return mVersion;
        }

        public int getMajor() {
            return getVersionPattern(mVersion, 1);
        }

        public int getMinor() {
            return getVersionPattern(mVersion, 2);
        }

        public int getPatch() {
            return getVersionPattern(mVersion, 3);
        }

        public String getDate() {
            return mDate;
        }
    }

}
