package com.rtchubs.restohubs.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rtchubs.restohubs.ViewModelFactory
import com.rtchubs.restohubs.ar_location.ARLocationViewModel
import com.rtchubs.restohubs.nid_scan.NIDScanCameraXViewModel
import com.rtchubs.restohubs.ui.LoginActivityViewModel
import com.rtchubs.restohubs.ui.MainActivityViewModel
import com.rtchubs.restohubs.ui.add_payment_methods.AddBankViewModel
import com.rtchubs.restohubs.ui.add_payment_methods.AddCardViewModel
import com.rtchubs.restohubs.ui.add_payment_methods.AddPaymentMethodsViewModel
import com.rtchubs.restohubs.ui.cart.CartViewModel
import com.rtchubs.restohubs.ui.chapter_list.ChapterListViewModel
import com.rtchubs.restohubs.ui.exams.ExamsViewModel
import com.rtchubs.restohubs.ui.home.*
import com.rtchubs.restohubs.ui.how_works.HowWorksViewModel
import com.rtchubs.restohubs.ui.info.InfoViewModel
import com.rtchubs.restohubs.ui.login.SignInViewModel
import com.rtchubs.restohubs.ui.tou.TouViewModel
import com.rtchubs.restohubs.ui.otp.OtpViewModel
import com.rtchubs.restohubs.ui.pre_on_boarding.PreOnBoardingViewModel
import com.rtchubs.restohubs.ui.profiles.ProfilesViewModel
import com.rtchubs.restohubs.ui.registration.RegistrationViewModel
import com.rtchubs.restohubs.ui.settings.SettingsViewModel
import com.rtchubs.restohubs.ui.setup.SetupViewModel
import com.rtchubs.restohubs.ui.setup_complete.SetupCompleteViewModel
import com.rtchubs.restohubs.ui.splash.SplashViewModel
import com.rtchubs.restohubs.ui.video_play.LoadWebViewViewModel
import com.rtchubs.restohubs.ui.video_play.VideoPlayViewModel
import com.rtchubs.restohubs.ui.login.ViewPagerViewModel
import com.rtchubs.restohubs.ui.more.MoreViewModel
import com.rtchubs.restohubs.ui.order.OrderViewModel
import com.rtchubs.restohubs.ui.otp_signin.OtpSignInViewModel
import com.rtchubs.restohubs.ui.pin_number.PinNumberViewModel
import com.rtchubs.restohubs.ui.profile_signin.ProfileSignInViewModel
import com.rtchubs.restohubs.ui.shops.ShopDetailsContactUsViewModel
import com.rtchubs.restohubs.ui.shops.ShopDetailsViewModel
import com.rtchubs.restohubs.ui.terms_and_conditions.TermsViewModel
import com.rtchubs.restohubs.ui.topup.TopUpAmountViewModel
import com.rtchubs.restohubs.ui.topup.TopUpBankCardViewModel
import com.rtchubs.restohubs.ui.topup.TopUpMobileViewModel
import com.rtchubs.restohubs.ui.topup.TopUpPinViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ViewModelModule {

    // PSB View Model Bind Here
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(LoginActivityViewModel::class)
    abstract fun bindLoginActivityViewModel(viewModel: LoginActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    abstract fun bindMainActivityViewModel(viewModel: MainActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MoreBookListViewModel::class)
    abstract fun bindMoreBookListViewModel(viewModel: MoreBookListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProductListViewModel::class)
    abstract fun bindProductListViewModel(viewModel: ProductListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CartViewModel::class)
    abstract fun bindCartViewModel(viewModel: CartViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavoriteViewModel::class)
    abstract fun bindFavoriteViewModel(viewModel: FavoriteViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProductDetailsViewModel::class)
    abstract fun bindProductDetailsViewModel(viewModel: ProductDetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MoreShoppingMallViewModel::class)
    abstract fun bindMoreShoppingMallViewModel(viewModel: MoreShoppingMallViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AllShopListViewModel::class)
    abstract fun bindAllShopListViewModel(viewModel: AllShopListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ShopDetailsViewModel::class)
    abstract fun bindShopDetailsViewModel(viewModel: ShopDetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ShopDetailsContactUsViewModel::class)
    abstract fun bindShopDetailsContactUsViewModel(viewModel: ShopDetailsContactUsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MoreViewModel::class)
    abstract fun bindMoreViewModel(viewModel: MoreViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SetAViewModel::class)
    abstract fun bindSetAViewModel(viewModel: SetAViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SetBViewModel::class)
    abstract fun bindSetBViewModel(viewModel: SetBViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SetCViewModel::class)
    abstract fun bindSetCViewModel(viewModel: SetCViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(InfoViewModel::class)
    abstract fun bindInfoViewModel(viewModel: InfoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ExamsViewModel::class)
    abstract fun bindExamsViewModel(viewModel: ExamsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindSplashViewModel(viewModel: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SignInViewModel::class)
    abstract fun bindSignInViewModel(viewModel: SignInViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NIDScanCameraXViewModel::class)
    abstract fun bindNIDScanCameraXViewModel(viewModel: NIDScanCameraXViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PreOnBoardingViewModel::class)
    abstract fun bindPreOnBoardingViewModel(viewModel: PreOnBoardingViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HowWorksViewModel::class)
    abstract fun bindHowWorksViewModel(viewModel: HowWorksViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(RegistrationViewModel::class)
    abstract fun bindRegistrationViewModel(viewModel: RegistrationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OtpViewModel::class)
    abstract fun bindOtpViewModel(viewModel: OtpViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(TouViewModel::class)
    abstract fun bindTouViewModel(viewModel: TouViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SetupViewModel::class)
    abstract fun bindSetupViewModel(viewModel: SetupViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SetupCompleteViewModel::class)
    abstract fun bindSetupCompleteViewModel(viewModel: SetupCompleteViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfilesViewModel::class)
    abstract fun bindSetupProfilesViewModel(viewModel: ProfilesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSetupSettingsViewModel(viewModel: SettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ViewPagerViewModel::class)
    abstract fun bindSetupViewPagerViewModel(viewModel: ViewPagerViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ChapterListViewModel::class)
    abstract fun bindSetupChapterListViewModel(viewModel: ChapterListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VideoPlayViewModel::class)
    abstract fun bindSetupVideoPlayViewModel(viewModel: VideoPlayViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoadWebViewViewModel::class)
    abstract fun bindSetupLoadWebViewViewModel(viewModel: LoadWebViewViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OtpSignInViewModel::class)
    abstract fun bindSetupOtpSignInViewModel(viewModel: OtpSignInViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PinNumberViewModel::class)
    abstract fun bindSetupPinNumberViewModel(viewModel: PinNumberViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileSignInViewModel::class)
    abstract fun bindSetupProfileSignInViewModel(viewModel: ProfileSignInViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TermsViewModel::class)
    abstract fun bindTermsViewModel(viewModel: TermsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddPaymentMethodsViewModel::class)
    abstract fun bindAddPaymentMethodsViewModel(viewModel: AddPaymentMethodsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddBankViewModel::class)
    abstract fun bindAddBankViewModel(viewModel: AddBankViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddCardViewModel::class)
    abstract fun bindAddCardViewModel(viewModel: AddCardViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TopUpMobileViewModel::class)
    abstract fun bindTopUpMobileViewModel(viewModel: TopUpMobileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TopUpAmountViewModel::class)
    abstract fun bindTopUpAmountViewModel(viewModel: TopUpAmountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TopUpPinViewModel::class)
    abstract fun bindTopUpPinViewModel(viewModel: TopUpPinViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TopUpBankCardViewModel::class)
    abstract fun bindTopUpBankCardViewModel(viewModel: TopUpBankCardViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ARLocationViewModel::class)
    abstract fun bindARLocationViewModel(viewModel: ARLocationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OrderViewModel::class)
    abstract fun bindOrderViewModel(viewModel: OrderViewModel): ViewModel
}