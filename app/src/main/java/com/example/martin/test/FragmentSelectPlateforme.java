package com.example.martin.test;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;



public class FragmentSelectPlateforme extends DialogFragment {
	private OnPlateformeSelectedListener mCallback;

	public FragmentSelectPlateforme() {
		// Required empty public constructor
	}



	public static FragmentSelectPlateforme newInstance() {
		return new FragmentSelectPlateforme();
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the pref for this fragment
		View view= inflater.inflate(R.layout.fragment_select_plateforme, container, false);
		Button btn = view.findViewById(R.id.ButtonSelectPlateforme);
		final RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				int i = radioGroup.getCheckedRadioButtonId();
				if (i!=-1) {
					if(i==R.id.radio1){
						i=0;
					}
					if(i==R.id.radio2){
						i=1;
					}
					if(i==R.id.radio3){
						i=2;
					}
					if(i==R.id.radio4){
						i=3;
					}
					if(i==R.id.radio5){
						i=4;
					}
					mCallback.onPlateformeSelected(i);
				}

			}
		});
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnPlateformeSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnHeadlineSelectedListener");
		}


	}

	// Container Activity must implement this interface
	public interface OnPlateformeSelectedListener {
		void onPlateformeSelected(int plateforme);
	}


}
