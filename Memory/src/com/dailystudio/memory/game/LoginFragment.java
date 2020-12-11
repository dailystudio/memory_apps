package com.dailystudio.memory.game;

import com.dailystudio.memory.R;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.Player;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class LoginFragment extends Fragment implements OnClickListener {

	public final static String FRAGMENT_TAG = "frg-gsm-login";
	
    public interface Listener {
        public void onSignInButtonClicked();
        public void onSignOutButtonClicked();
        public void onShowAchievementsRequested();
        public void onShowLeaderBoardsRequested();
    }
   
    Listener mListener = null;
    boolean mShowSignIn = true;
    
    private Uri mPlayerIconUri;
    private ImageManager mImageManager;

    private ImageView mPlayerIconView;
    private TextView mPlayerNameView;
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	
    	mImageManager = ImageManager.create(getActivity());
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gms_login_bar, 
        		container, false);
        
        final int[] CLICKABLES = new int[] {
                R.id.sign_in_button, R.id.sign_out_button,
                R.id.menu_achievements,
                R.id.menu_leaderboards,
        };
        
        for (int i : CLICKABLES) {
            v.findViewById(i).setOnClickListener(this);
        }
        
        LayoutParams lp = new LayoutParams(
    		   LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        
        lp.addRule(RelativeLayout.ALIGN_BOTTOM);
        
        v.setLayoutParams(lp);
        
        mPlayerIconView = (ImageView) v.findViewById(R.id.player_icon);
        mPlayerNameView = (TextView) v.findViewById(R.id.player_name);
        
        return v;
    }

    @Override
    public void onStart() {
    	super.onStart();
    	
    	updateUi();
    }
    
    public void setListener(Listener l) {
        mListener = l;
    }

    void updateUi() {
        if (getActivity() == null) return;
        getActivity().findViewById(R.id.sign_in_bar).setVisibility(mShowSignIn ?
                View.VISIBLE : View.GONE);
        getActivity().findViewById(R.id.sign_out_bar).setVisibility(mShowSignIn ?
                View.GONE : View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
    	if (mListener == null) {
    		return;
    	}
    	
        switch (view.getId()) {
	        case R.id.sign_in_button:
	            mListener.onSignInButtonClicked();
	            break;
	            
	        case R.id.sign_out_button:
	            mListener.onSignOutButtonClicked();
	            break;
	            
	        case R.id.menu_achievements:
	            mListener.onShowAchievementsRequested();
	            break;
	            
	        case R.id.menu_leaderboards:
	            mListener.onShowLeaderBoardsRequested();
	            break;
        }
    }

    public void setShowSignInButton(boolean showSignIn) {
        mShowSignIn = showSignIn;
    
        updateUi();
    }
    
    public void setPlayerInfo(Player p) {
    	if (p == null) {
    		return;
    	}
    	
    	Uri iconUri = p.getHiResImageUri();
    	if (mPlayerIconUri == null 
    			|| !mPlayerIconUri.equals(iconUri)) {
    		mImageManager.loadImage(mPlayerIconView, iconUri,
    				R.drawable.default_player_icon);
    	}
    	
    	mPlayerNameView.setText(p.getDisplayName());
    	
    	mPlayerIconUri = iconUri;
    }
}
