package com.kbs.geo.coastal.http.interceptor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.io.output.TeeOutputStream;

/**
 * Allows HttpServletResponse body to be consumed more than once
 * @author Steve
 *
 */
public class BufferedHttpServletResponse extends HttpServletResponseWrapper {
	TeeServletOutputStream teeStream;

	PrintWriter teeWriter;

	ByteArrayOutputStream bos;
	
	public BufferedHttpServletResponse(HttpServletResponse response, ByteArrayOutputStream bos) {
		super(response);
		this.bos = bos;
	}

	public String getBody() throws IOException {
		return bos.toString();
	}

	@Override
	public PrintWriter getWriter() throws IOException {

		if (this.teeWriter == null) {
			this.teeWriter = new PrintWriter(new OutputStreamWriter(getOutputStream()));
		}
		return this.teeWriter;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {

		if (teeStream == null) {
			if(null == bos) bos = new ByteArrayOutputStream();
			teeStream = new TeeServletOutputStream(getResponse().getOutputStream(), bos);
		}
		return teeStream;
	}

	@Override
	public void flushBuffer() throws IOException {
		if (teeStream != null) {
			teeStream.flush();
		}
		if (this.teeWriter != null) {
			this.teeWriter.flush();
		}
	}

	public class TeeServletOutputStream extends ServletOutputStream {

		private final TeeOutputStream targetStream;

		public TeeServletOutputStream(OutputStream one, OutputStream two) {
			targetStream = new TeeOutputStream(one, two);
		}

		@Override
		public void write(int arg0) throws IOException {
			this.targetStream.write(arg0);
		}

		public void flush() throws IOException {
			super.flush();
			this.targetStream.flush();
		}

		public void close() throws IOException {
			super.close();
			this.targetStream.close();
		}
	}
}
