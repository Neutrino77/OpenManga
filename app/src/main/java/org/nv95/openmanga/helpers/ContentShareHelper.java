package org.nv95.openmanga.helpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;

import org.nv95.openmanga.R;
import org.nv95.openmanga.activities.PreviewActivity2;
import org.nv95.openmanga.items.MangaInfo;
import org.nv95.openmanga.providers.LocalMangaProvider;
import org.nv95.openmanga.utils.ImageUtils;
import org.nv95.openmanga.utils.LayoutUtils;

import java.io.File;

/**
 * Created by nv95 on 28.01.16.
 */
public class ContentShareHelper {

    private final Context mContext;
    private final Intent mIntent;

    public ContentShareHelper(Context context) {
        mContext = context;
        mIntent = new Intent(Intent.ACTION_SEND);
    }

    public void share(MangaInfo manga) {
        mIntent.setType("text/plain");
        String path = null;
        if (manga.provider == LocalMangaProvider.class) {
            path = LocalMangaProvider.getInstance(mContext).getSourceUrl(manga.id);
        }
        if (path == null) {
            path = manga.path;
        }
        mIntent.putExtra(Intent.EXTRA_TEXT, path);
        mIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, manga.name);
        mContext.startActivity(Intent.createChooser(mIntent, mContext.getString(R.string.action_share)));
    }

    public void shareImage(File file) {
        mIntent.setType("image/*");
        mIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        mContext.startActivity(Intent.createChooser(mIntent, mContext.getString(R.string.action_share)));
    }

    public void exportFile(File file) {
        mIntent.setType("file/*");
        mIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        mContext.startActivity(Intent.createChooser(mIntent, mContext.getString(R.string.export_file)));
    }

    public void createShortcut(MangaInfo manga) {
        Intent shortcutIntent = new Intent(mContext, PreviewActivity2.class);
        shortcutIntent.setAction("org.nv95.openmanga.action.PREVIEW");
        shortcutIntent.putExtras(manga.toBundle());
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, manga.name);

        Bitmap cover = ImageUtils.getCachedImage(manga.preview);
        if (cover == null) {
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(mContext, R.mipmap.ic_launcher));
        } else {
            final int size = LayoutUtils.getLauncherIconSize(mContext);
            cover = ThumbnailUtils.extractThumbnail(cover, size, size, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, cover);
        }
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        mContext.getApplicationContext().sendBroadcast(addIntent);
    }
}
