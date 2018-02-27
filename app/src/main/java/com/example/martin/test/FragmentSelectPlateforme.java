package com.example.martin.test;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import static com.example.martin.test.Value.IND_PLATEFORME_1;
import static com.example.martin.test.Value.IND_PLATEFORME_2;
import static com.example.martin.test.Value.IND_PLATEFORME_3;
import static com.example.martin.test.Value.IND_PLATEFORME_4;


public class FragmentSelectPlateforme extends DialogFragment {
	OnPlateformeSelectedListener mCallback;

	public FragmentSelectPlateforme() {
		// Required empty public constructor
	}



	public static FragmentSelectPlateforme newInstance() {
		return new FragmentSelectPlateforme();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view= inflater.inflate(R.layout.fragment_select_plateforme, container, false);
		Button btn = view.findViewById(R.id.ButtonSelectPlateforme);
		final RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				int i = radioGroup.getCheckedRadioButtonId();
				if (i!=-1) {
					switch (i) {
						case R.id.radio1:
							i = IND_PLATEFORME_1;
							break;
						case R.id.radio2:
							i = IND_PLATEFORME_2;
							break;
						case R.id.radio3:
							i = IND_PLATEFORME_3;
							break;
						case R.id.radio4:
							i = IND_PLATEFORME_4;
							break;
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

	@Override
	public void onDetach() {
		super.onDetach();
	}
	// Container Activity must implement this interface
	public interface OnPlateformeSelectedListener {
		void onPlateformeSelected(int plateforme);
	}


}
