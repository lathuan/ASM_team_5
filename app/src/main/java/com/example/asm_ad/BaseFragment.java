package com.example.asm_ad;

import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {
    protected MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }
}
