package com.brightplan.recs.filter;

import com.google.common.primitives.Bytes;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * RecsHttpRequestWrapper allows to read the request body multiple times. It is especially useful to
 * investigate pager-duty alerts.
 */
public class RecsHttpRequestWrapper extends HttpServletRequestWrapper {

  // Keep the request body
  private byte[] requestBody = new byte[0];
  // Reads request body only once
  private boolean bufferFilled = false;
  private boolean multipleRequestBodyRead = true;

  public RecsHttpRequestWrapper(HttpServletRequest request) {
    super(request);
  }

  public byte[] getRequestBody() throws IOException {
    if (bufferFilled) {
      return Arrays.copyOf(requestBody, requestBody.length);
    }

    InputStream inputStream = super.getInputStream();

    byte[] buffer = new byte[1024];

    int bytesRead;
    while ((bytesRead = inputStream.read(buffer)) != -1) {
      requestBody = Bytes.concat(this.requestBody, Arrays.copyOfRange(buffer, 0, bytesRead));
    }

    bufferFilled = true;

    return requestBody;
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    if (multipleRequestBodyRead) {
      return new RecsCustomServletInputStream(getRequestBody());
    }
    return super.getInputStream();
  }

  public ServletInputStream getInputStreamCopy() throws IOException {
    multipleRequestBodyRead = true;
    return new RecsCustomServletInputStream(getRequestBody());
  }
}


class RecsCustomServletInputStream extends ServletInputStream {

  private ByteArrayInputStream buffer;

  public RecsCustomServletInputStream(byte[] contents) {
    this.buffer = new ByteArrayInputStream(contents);
  }

  @Override
  public int read() throws IOException {
    return buffer.read();
  }

  @Override
  public boolean isFinished() {
    return buffer.available() == 0;
  }

  @Override
  public boolean isReady() {
    return true;
  }

  @Override
  public void setReadListener(ReadListener listener) {
    throw new RuntimeException("Not implemented");
  }
}

