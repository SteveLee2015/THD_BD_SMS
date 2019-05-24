package thd.bd.sms.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Fragment适配器
 * @author llg052
 *
 */
public class FragmentAdapter extends FragmentStatePagerAdapter {

	private List<Fragment> fragments;

//	public FragmentAdapter(FragmentManager fm) {
//		super(fm);
//	}

	@Override
	public Fragment getItem(int arg0) {
		/**
		 * fragmentsList.add(new MsgBDFragment());
//			fragmentsList.add(new InstructNaviFragment());
//			fragmentsList.add(new LineFragment());
//			fragmentsList.add(new ContainerFragment());
		 */
//		switch (arg0) {
//		case 0:
//			return new MsgBDFragment();
//		case 1:
//			return new InstructNaviFragment();
//		case 2:
//			return new LineFragment();
//		case 3:
//			return new ContainerFragment();
//		
//
//		default:
//			break;
//		}
		return fragments.get(arg0);
	}

	public FragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		
		this.fragments = fragments;
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

}
