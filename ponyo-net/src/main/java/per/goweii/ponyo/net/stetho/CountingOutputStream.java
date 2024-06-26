package per.goweii.ponyo.net.stetho;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class CountingOutputStream extends FilterOutputStream {
  private long mCount;

  public CountingOutputStream(OutputStream out) {
    super(out);
  }

  public long getCount() {
    return mCount;
  }

  @Override
  public void write(int oneByte) throws IOException {
    out.write(oneByte);
    mCount++;
  }

  @Override
  public void write(byte[] buffer) throws IOException {
    write(buffer, 0, buffer.length);
  }

  @Override
  public void write(byte[] buffer, int offset, int length) throws IOException {
    out.write(buffer, offset, length);
    mCount += length;
  }
}
