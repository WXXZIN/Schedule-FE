package com.android.schedule.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

import com.android.schedule.ui.fragment.MainFragment;
import com.android.schedule.ui.fragment.ClassroomScheduleFragment;
import com.android.schedule.ui.fragment.ScheduleFragment;

public class PageAdapter extends FragmentStateAdapter {

    private final List<Fragment> fragmentList = new ArrayList<>();

    public PageAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        fragmentList.add(new MainFragment());
        fragmentList.add(new ScheduleFragment());
        fragmentList.add(new ClassroomScheduleFragment());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
