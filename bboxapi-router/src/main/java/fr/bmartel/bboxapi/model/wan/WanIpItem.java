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
package fr.bmartel.bboxapi.model.wan;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Wan IP Item.
 *
 * @author Bertrand Martel
 */
public class WanIpItem {

    @SerializedName("address")
    private String mAddress;

    @SerializedName("state")
    private String mState;

    @SerializedName("gateway")
    private String mGateway;

    @SerializedName("dnsservers")
    private String mDnsServers;

    @SerializedName("subnet")
    private String mSubnet;

    @SerializedName("ip6state")
    private String mIp6State;

    @SerializedName("ip6address")
    private List<IpAddress> mIp6Address;

    @SerializedName("ip6prefix")
    private List<IpAddress> mIp6Prefix;

    @SerializedName("mac")
    private String mMac;

    @SerializedName("mtu")
    private int mMtu;

    public String getAddress() {
        return mAddress;
    }

    public String getState() {
        return mState;
    }

    public String getGateway() {
        return mGateway;
    }

    public String getDnsServers() {
        return mDnsServers;
    }

    public String getSubnet() {
        return mSubnet;
    }

    public String getIp6State() {
        return mIp6State;
    }

    public List<IpAddress> getIp6Address() {
        return mIp6Address;
    }

    public List<IpAddress> getIp6Prefix() {
        return mIp6Prefix;
    }

    public String getMac() {
        return mMac;
    }

    public int getMtu() {
        return mMtu;
    }
}
