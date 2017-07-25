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
package fr.bmartel.bboxapi.model;

/**
 * Status returned by HttpResponse object.
 *
 * @author Bertrand Martel
 */
public enum HttpStatus {

    OK(200),
    CREATED(201),
    TOO_MANY_REQUEST(429),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    INTERNAL_ERROR(500),
    NOT_MODIFIED(304),
    UNKNOWN(-1);

    private int mStatus;

    HttpStatus(int status) {
        mStatus = status;
    }

    public int getCode() {
        return mStatus;
    }

    public static HttpStatus gethttpStatus(int status) {
        for (HttpStatus httpStatus : HttpStatus.values()) {
            if (status == httpStatus.getCode())
                return httpStatus;
        }
        return HttpStatus.UNKNOWN;
    }
}
