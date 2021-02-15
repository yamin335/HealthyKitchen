package com.rtchubs.restohubs.di

import com.rtchubs.restohubs.ar_location.ARLocationFragment
import com.rtchubs.restohubs.nid_scan.NIDScanCameraXFragment
import com.rtchubs.restohubs.ui.add_payment_methods.AddBankFragment
import com.rtchubs.restohubs.ui.add_payment_methods.AddCardFragment
import com.rtchubs.restohubs.ui.add_payment_methods.AddPaymentMethodsFragment
import com.rtchubs.restohubs.ui.cart.CartFragment
import com.rtchubs.restohubs.ui.chapter_list.ChapterListFragment
import com.rtchubs.restohubs.ui.exams.ExamsFragment
import com.rtchubs.restohubs.ui.home.*
import com.rtchubs.restohubs.ui.how_works.HowWorksFragment
import com.rtchubs.restohubs.ui.info.InfoFragment
import com.rtchubs.restohubs.ui.login.SignInFragment
import com.rtchubs.restohubs.ui.terms_and_conditions.TermsAndConditionsFragment
import com.rtchubs.restohubs.ui.tou.TouFragment
import com.rtchubs.restohubs.ui.otp.OtpFragment
import com.rtchubs.restohubs.ui.pre_on_boarding.PreOnBoardingFragment
import com.rtchubs.restohubs.ui.profiles.ProfilesFragment
import com.rtchubs.restohubs.ui.registration.RegistrationFragment
import com.rtchubs.restohubs.ui.settings.SettingsFragment
import com.rtchubs.restohubs.ui.setup.SetupFragment
import com.rtchubs.restohubs.ui.setup_complete.SetupCompleteFragment
import com.rtchubs.restohubs.ui.splash.SplashFragment
import com.rtchubs.restohubs.ui.video_play.LoadWebViewFragment
import com.rtchubs.restohubs.ui.video_play.VideoPlayFragment
import com.rtchubs.restohubs.ui.login.ViewPagerFragment
import com.rtchubs.restohubs.ui.more.MoreFragment
import com.rtchubs.restohubs.ui.order.OrderDialogFragment
import com.rtchubs.restohubs.ui.otp_signin.OtpSignInFragment
import com.rtchubs.restohubs.ui.pin_number.PinNumberFragment
import com.rtchubs.restohubs.ui.profile_signin.ProfileSignInFragment
import com.rtchubs.restohubs.ui.shops.ShopDetailsContactUsFragment
import com.rtchubs.restohubs.ui.shops.ShopDetailsFragment
import com.rtchubs.restohubs.ui.shops.ShopDetailsProductListFragment
import com.rtchubs.restohubs.ui.topup.TopUpAmountFragment
import com.rtchubs.restohubs.ui.topup.TopUpBankCardFragment
import com.rtchubs.restohubs.ui.topup.TopUpMobileFragment
import com.rtchubs.restohubs.ui.topup.TopUpPinFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeSplashFragment(): SplashFragment

    @ContributesAndroidInjector
    abstract fun contributeTermsAndConditionsFragment(): TermsAndConditionsFragment

    @ContributesAndroidInjector
    abstract fun contributeMoreBookListFragment(): MoreBookListFragment

    @ContributesAndroidInjector
    abstract fun contributeMoreShoppingMallFragment(): MoreShoppingMallFragment

    @ContributesAndroidInjector
    abstract fun contributeAllShopListFragment(): AllShopListFragment

    @ContributesAndroidInjector
    abstract fun contributeShopDetailsFragment(): ShopDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributeShopDetailsProductListFragment(): ShopDetailsProductListFragment

    @ContributesAndroidInjector
    abstract fun contributeShopDetailsContactUsFragment(): ShopDetailsContactUsFragment

    @ContributesAndroidInjector
    abstract fun contributeProductListFragment(): ProductListFragment

    @ContributesAndroidInjector
    abstract fun contributeProductDetailsFragment(): ProductDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributeCartFragment(): CartFragment

    @ContributesAndroidInjector
    abstract fun contributeFavoriteFragment(): FavoriteFragment

    @ContributesAndroidInjector
    abstract fun contributeSignInFragment(): SignInFragment

    @ContributesAndroidInjector
    abstract fun contributeExamsFragment(): ExamsFragment

    @ContributesAndroidInjector
    abstract fun contributeInfoFragment(): InfoFragment

    @ContributesAndroidInjector
    abstract fun contributeNIDScanCameraXFragment(): NIDScanCameraXFragment

    @ContributesAndroidInjector
    abstract fun contributePreOnBoardingFragment(): PreOnBoardingFragment

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeMoreFragment(): MoreFragment

    @ContributesAndroidInjector
    abstract fun contributeSetAFragment(): SetAFragment

    @ContributesAndroidInjector
    abstract fun contributeSetBFragment(): SetBFragment

    @ContributesAndroidInjector
    abstract fun contributeSetCFragment(): SetCFragment

    @ContributesAndroidInjector
    abstract fun contributeHome2Fragment(): Home2Fragment

    @ContributesAndroidInjector
    abstract fun contributeAddPaymentMethodsFragment(): AddPaymentMethodsFragment


    @ContributesAndroidInjector
    abstract fun contributeHowWorksFragment(): HowWorksFragment

    @ContributesAndroidInjector
    abstract fun contributeRegistrationFragment(): RegistrationFragment

    @ContributesAndroidInjector
    abstract fun contributeOtpFragment(): OtpFragment

    @ContributesAndroidInjector
    abstract fun contributeTouFragment(): TouFragment

    @ContributesAndroidInjector
    abstract fun contributeSetupFragment(): SetupFragment

    @ContributesAndroidInjector
    abstract fun contributeSetupCompleteFragment(): SetupCompleteFragment

    @ContributesAndroidInjector
    abstract fun contributeProfilesFragment(): ProfilesFragment

    @ContributesAndroidInjector
    abstract fun contributeSettingsFragment(): SettingsFragment

    @ContributesAndroidInjector
    abstract fun contributeViewPagerFragment(): ViewPagerFragment

    @ContributesAndroidInjector
    abstract fun contributeChapterListFragment(): ChapterListFragment

    @ContributesAndroidInjector
    abstract fun contributeVideoPlayFragment(): VideoPlayFragment

    @ContributesAndroidInjector
    abstract fun contributeLoadWebViewFragment(): LoadWebViewFragment

    @ContributesAndroidInjector
    abstract fun contributeOtpSignInFragment(): OtpSignInFragment

    @ContributesAndroidInjector
    abstract fun contributePinNumberFragment(): PinNumberFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileSignInFragment(): ProfileSignInFragment

    @ContributesAndroidInjector
    abstract fun contributeAddBankFragment(): AddBankFragment

    @ContributesAndroidInjector
    abstract fun contributeAddCardFragment(): AddCardFragment

    @ContributesAndroidInjector
    abstract fun contributeTopUpMobileFragment(): TopUpMobileFragment

    @ContributesAndroidInjector
    abstract fun contributeTopUpAmountFragment(): TopUpAmountFragment

    @ContributesAndroidInjector
    abstract fun contributeTopUpPinFragment(): TopUpPinFragment

    @ContributesAndroidInjector
    abstract fun contributeTopUpBankCardFragment(): TopUpBankCardFragment

    @ContributesAndroidInjector
    abstract fun contributeARLocationFragment(): ARLocationFragment

    @ContributesAndroidInjector
    abstract fun contributeOrderFragment(): OrderDialogFragment
}