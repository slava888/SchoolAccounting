package de.slava.schoolaccounting.manage.children;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import de.slava.schoolaccounting.model.Child;
import de.slava.schoolaccounting.room.RoomChildItem;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Child} and makes a call to the
 * specified {@link de.slava.schoolaccounting.manage.children.ManageChildrenFragment.OnListFragmentInteractionListener}.
 */
public class ChildRecyclerViewAdapter extends RecyclerView.Adapter<ChildRecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private List<Child> items;
    private final ManageChildrenFragment.OnListFragmentInteractionListener mListener;

    public ChildRecyclerViewAdapter(Context context, List<Child> items, ManageChildrenFragment.OnListFragmentInteractionListener listener) {
        this.context = context;
        this.items = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new RoomChildItem(context, null));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(items.get(position));
        holder.view.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onListFragmentInteraction(holder.item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<Child> items) {
        this.items = items;
        // TODO: update visuals?
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final RoomChildItem view;
        private Child item;

        public ViewHolder(RoomChildItem view) {
            super(view);
            this.view = view;
        }

        public void setItem(Child item) {
            this.item = item;
            view.dataInit(item);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
