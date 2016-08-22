package de.slava.schoolaccounting.manage.children;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.slava.schoolaccounting.Main;
import de.slava.schoolaccounting.R;
import de.slava.schoolaccounting.model.Image;
import de.slava.schoolaccounting.model.db.EntityManager;
import de.slava.schoolaccounting.model.db.ImageDao;

/**
 * @author by V.Sysoltsev
 */
public class ManageChildImageDialogFragment extends DialogFragment {

    @Bind(R.id.imgGrid) GridView imgGrid;

    private ImageClickListener clickListener;

    private EntityManager getDb() {
        return EntityManager.instance(getContext());
    }

    /**
     * A listener for click on image.
     */
    public static interface ImageClickListener {
        /**
         * Will be called when user clicks on an image. Returning 'true' means dismiss dialog, 'false' continues it.
         * @param image
         * @return
         */
        public boolean handleClick(Image image);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_manage_child_image, null);
        ButterKnife.bind(this, view);
        populateImages(imgGrid);
        builder.setTitle(R.string.manage_child_image_title)
                .setView(view)
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    Log.d(Main.getTag(), "Cancel");
                });
        return builder.create();
    }

    private void populateImages(GridView grid) {
        List<Image> images = getDb().getDao(ImageDao.class).findAllForPersons();
        grid.setAdapter(new ImageAdapter(getContext(), images));
    }

    private class ImageAdapter extends BaseAdapter {
        private final Context context;
        private final List<Image> images;

        public ImageAdapter(Context context, List<Image> images) {
            this.context = context;
            this.images = images;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object getItem(int position) {
            return images.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(context);
                imageView.setLayoutParams(new GridView.LayoutParams(185, 185));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(5, 5, 5, 5);
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setImageResource(images.get(position).getSid().getResourceId());
            imageView.setOnClickListener(view -> {
                if (getClickListener() == null)
                    return;
                boolean dismiss = getClickListener().handleClick(images.get(position));
                if (dismiss)
                    dismiss();
            });
            return imageView;
        }
    }

    public void setClickListener(ImageClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public ImageClickListener getClickListener() {
        return clickListener;
    }
}
