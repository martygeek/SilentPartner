package com.martypants.silentpartner;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.martypants.silentpartner.models.FixedSize;
import com.martypants.silentpartner.models.GIF;

import java.io.InputStream;

/**
 * A model loader that translates a POJO mirroring a JSON object representing a single image from
 * Giphy's api into an {@link InputStream} that can be decoded into an {@link
 * android.graphics.drawable.Drawable}.
 */
public final class GiphyModelLoader extends BaseGlideUrlLoader<GIF> {

  @Override
  public boolean handles(@NonNull GIF model) {
    return true;
  }

  private GiphyModelLoader(ModelLoader<GlideUrl, InputStream> urlLoader) {
    super(urlLoader);
  }

  @Override
  protected String getUrl(GIF model, int width, int height, Options options) {
    FixedSize fixedHeight = model.getData().get(0).getImages().getFixed_height();
    int fixedHeightDifference = getDifference(fixedHeight, width, height);
    FixedSize fixedWidth = model.getData().get(0).getImages().getFixed_width();
    int fixedWidthDifference = getDifference(fixedWidth, width, height);
    if (fixedHeightDifference < fixedWidthDifference && !TextUtils.isEmpty(fixedHeight.getUrl())) {
      return fixedHeight.getUrl();
    } else if (!TextUtils.isEmpty(fixedWidth.getUrl())) {
      return fixedWidth.getUrl();
    } else if (!TextUtils.isEmpty(model.getData().get(0).getImages().getOriginal().getUrl())) {
      return model.getData().get(0).getImages().getOriginal().getUrl();
    } else {
      return null;
    }
  }

  private static int getDifference(FixedSize gifImage, int width, int height) {
    return Math.abs(width - gifImage.getWidth()) + Math.abs(height - gifImage.getHeight());
  }

  /* The default factory for {@link com.bumptech.glide.samples.giphy.GiphyModelLoader}s. */
  public static final class Factory implements ModelLoaderFactory<GIF, InputStream> {
    @NonNull
    @Override
    public ModelLoader<GIF, InputStream> build(MultiModelLoaderFactory multiFactory) {
      return new GiphyModelLoader(multiFactory.build(GlideUrl.class, InputStream.class));
    }

    @Override
    public void teardown() {
      // Do nothing.
    }
  }
}
