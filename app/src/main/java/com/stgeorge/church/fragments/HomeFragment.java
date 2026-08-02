package com.stgeorge.church.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.stgeorge.church.R;
import com.stgeorge.church.activities.AgpeyaListActivity;
import com.stgeorge.church.activities.AnnouncementsListActivity;
import com.stgeorge.church.activities.ComingSoonActivity;
import com.stgeorge.church.activities.TafsirHomeActivity;
import com.stgeorge.church.firebase.AnnouncementRepository;
import com.stgeorge.church.models.Announcement;

import java.util.List;

/**
 * الصفحة الرئيسية (section 1) — the fragment MainActivity shows by default
 * for every role. Verse-of-the-day and mass schedule are static for now
 * (they need their own admin-editable Firestore doc, planned for the
 * Admin Dashboard stage); "آخر إعلان" is already live from Firestore via
 * {@link AnnouncementRepository}, and the quick-access chips jump straight
 * into the sections people use most.
 */
public class HomeFragment extends Fragment {

    private android.widget.TextView tvLatestAnnouncement;
    private final AnnouncementRepository announcementRepository = new AnnouncementRepository();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvLatestAnnouncement = view.findViewById(R.id.tvLatestAnnouncement);
        ChipGroup chipGroup = view.findViewById(R.id.chipGroupQuickAccess);

        addQuickAccessChip(chipGroup, R.string.nav_announcements,
                () -> startActivity(new Intent(getContext(), AnnouncementsListActivity.class)));
        addQuickAccessChip(chipGroup, R.string.nav_agpeya,
                () -> startActivity(new Intent(getContext(), AgpeyaListActivity.class)));
        addQuickAccessChip(chipGroup, R.string.nav_bible,
                () -> startActivity(new Intent(getContext(), TafsirHomeActivity.class)));
        addQuickAccessChip(chipGroup, R.string.nav_events,
                () -> startActivity(ComingSoonActivity.newIntent(getContext(), getString(R.string.nav_events))));

        loadLatestAnnouncement();
    }

    private void addQuickAccessChip(ChipGroup chipGroup, int titleRes, Runnable onClick) {
        Chip chip = new Chip(requireContext());
        chip.setText(titleRes);
        chip.setClickable(true);
        chip.setCheckable(false);
        chip.setOnClickListener(v -> onClick.run());
        chipGroup.addView(chip);
    }

    private void loadLatestAnnouncement() {
        announcementRepository.getLatest(new AnnouncementRepository.ListCallback() {
            @Override
            public void onSuccess(List<Announcement> announcements) {
                if (!isAdded() || tvLatestAnnouncement == null) {
                    return;
                }
                if (!announcements.isEmpty()) {
                    Announcement latest = announcements.get(0);
                    tvLatestAnnouncement.setText(latest.getTitle() + "\n" + latest.getBody());
                } else {
                    tvLatestAnnouncement.setText(R.string.home_no_announcements);
                }
            }

            @Override
            public void onFailure(String message) {
                // Keep the default "no announcements" placeholder already in the layout —
                // a transient Firestore error on the home screen shouldn't show an alarming error.
            }
        });
    }
}
