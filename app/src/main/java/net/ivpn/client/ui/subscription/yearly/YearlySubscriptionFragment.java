package net.ivpn.client.ui.subscription.yearly;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.billingclient.api.SkuDetails;

import net.ivpn.client.R;
import net.ivpn.client.databinding.FragmentYearlySubscriptionBinding;
import net.ivpn.client.ui.subscription.SubscriptionActivity;
import net.ivpn.client.ui.subscription.SubscriptionViewModel;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class YearlySubscriptionFragment extends Fragment {
    private static final Logger LOGGER = LoggerFactory.getLogger(YearlySubscriptionFragment.class);

    private FragmentYearlySubscriptionBinding binding;
    @Inject SubscriptionViewModel viewmodel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((SubscriptionActivity) context).activityComponent.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_yearly_subscription, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        binding.subscriptionDiscount.setText(Html.fromHtml("Save " +
                "<b><font color=\"#FF0000\"> 20% </font></b> with annual subscription"));
        binding.setViewmodel(viewmodel);
    }
}