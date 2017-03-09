package model;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.apache.log4j.Logger;

public abstract class ItemizedOverlay<Item extends OverlayItem> {

	static final Logger logger = Logger.getLogger(ItemizedOverlay.class);

	public static final String VIDEO = "video";

	public static final String TRACK = "track";

	public static final String PLOTS = "plot";

	protected ConcurrentLinkedDeque<Item> mInternalItemList;

	protected Deque<Item> mDequeItemList;

	protected List<Item> mItemList;

	private String type;

	public ItemizedOverlay(String name) {

		type = name;

		if (type != null && !type.isEmpty())

			switch (type) {

			case VIDEO:

				mInternalItemList = new ConcurrentLinkedDeque<Item>();

				break;

			case TRACK:

				mItemList = new ArrayList<Item>();

				break;

			case PLOTS:

				mItemList = new ArrayList<Item>();

				break;

			default:

				break;

			}

	}

	public void addOverlayItemList(final int location, final Item item) {

		mItemList.add(location, item);

	}

	public List<Item> getOverlayItemList() {

		return mItemList;

	}

	public Item getOverlayItemList(final int location) {

		if (location < mItemList.size())

			return mItemList.get(location);

		return null;

	}

	public Item removeOverlayItemList(final int location) {

		return mItemList.remove(location);

	}

	public void setOverlayItemList(final int location, final Item item) {

		mItemList.set(location, item);

	}

	public boolean addOverlayItem(final Item item) {

		switch (type) {

		case VIDEO:

			return mInternalItemList.add(item);

		case PLOTS:

			return mItemList.add(item);

		}

		return false;

	}

	public synchronized Iterator<Item> getOverlayIterator() {

		switch (type) {

		case VIDEO:

			return mInternalItemList.iterator();

		}

		return null;

	}

	public Item removeOverlayItem() {

		switch (type) {

		case VIDEO:

			return mInternalItemList.remove();

		}

		return null;

	}

	public void clear() {

		switch (type) {

		case VIDEO:

			mInternalItemList.clear();

			break;

		case PLOTS:

			mItemList.clear();

			break;

		case TRACK:

			mItemList.clear();

			break;

		}

	}

	public final int size() {

		switch (type) {

		case VIDEO:

			return mInternalItemList.size();

		case PLOTS:

			return mItemList.size();

		case TRACK:

			return mItemList.size();

		}

		return 0;

	}

}