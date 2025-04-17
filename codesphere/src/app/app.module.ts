import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from './shared/material-module';
import { FlexLayoutModule } from '@angular/flex-layout';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import { SignupComponent } from './signup/signup.component';
import {NgxUiLoaderConfig, NgxUiLoaderModule, SPINNER} from "ngx-ui-loader";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatIconModule} from "@angular/material/icon";
import { VerifyComponent } from './material-component/dialog/verify/verify.component';
import { LoginComponent } from './login/login.component';
import { ForgotPasswordComponent } from './material-component/dialog/forgot-password/forgot-password.component';
import { SetPasswordComponent } from './material-component/dialog/set-password/set-password.component';
import { VerifyForgotPasswordComponent } from './material-component/dialog/verify-forgot-password/verify-forgot-password.component';
import { ConfirmationComponent } from './material-component/dialog/confirmation/confirmation.component';
import { ChangePasswordComponent } from './material-component/dialog/change-password/change-password.component';
import {TokenInterceptorInterceptor} from "./services/interceptor/token-interceptor.interceptor";
import { UnauthorizedComponent } from './unauthorized/unauthorized.component';
import {SharedModule} from "./shared/shared.module";
import {DashboardModule} from "./dashboard/dashboard.module";
import {SharedQuillModule} from "./shared/quill/quill.module";
import { MonacoModule } from './shared/monaco/monaco.module';
import { CourseRsComponent } from './course-rs/course-rs.component';
import { CourseDetailsComponent } from './tour-details/course-details.component';
import { CartComponent } from './cart/cart.component';
import { SuccessComponent } from './payment/success/success.component';
import { CancelComponent } from './payment/cancel/cancel.component';
import { ListComponent } from './access-course/list/list.component';
import { DetailComponent } from './access-course/detail/detail.component';
import {VgBufferingModule} from "@videogular/ngx-videogular/buffering";
import {VgControlsModule} from "@videogular/ngx-videogular/controls";
import {VgCoreModule} from "@videogular/ngx-videogular/core";
import {VgOverlayPlayModule} from "@videogular/ngx-videogular/overlay-play";
import {SharedChartsModule} from "./shared/charts/charts.module";
import { OrderFormComponent } from './order-form/order-form.component';
const ngxUiLoaderConfig: NgxUiLoaderConfig = {
  "bgsColor": "#168da5",
  "bgsOpacity": 0.5,
  "bgsPosition": "bottom-right",
  "bgsSize": 60,
  "bgsType": "ball-spin-clockwise",
  "blur": 5,
  "delay": 0,
  "fastFadeOut": true,
  "fgsColor": "#168da5",
  "fgsPosition": "center-center",
  "fgsSize": 60,
  "fgsType": "ball-spin-clockwise",
  "gap": 24,
  "logoPosition": "center-center",
  "logoSize": 120,
  "logoUrl": "",
  "masterLoaderId": "master",
  "overlayBorderRadius": "0",
  "overlayColor": "rgba(40, 40, 40, 0.8)",
  "pbColor": "#168da5",
  "pbDirection": "ltr",
  "pbThickness": 3,
  "hasProgressBar": true,
  "text": "Đang tải",
  "textColor": "#FFFFFF",
  "textPosition": "center-center",
  "maxTime": -1,
  "minTime": 200
}

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    HeaderComponent,
    FooterComponent,
    SignupComponent,
    VerifyComponent,
    LoginComponent,
    ForgotPasswordComponent,
    SetPasswordComponent,
    VerifyForgotPasswordComponent,
    ConfirmationComponent,
    ChangePasswordComponent,
    UnauthorizedComponent,
    CourseRsComponent,
    CourseDetailsComponent,
    CartComponent,
    SuccessComponent,
    CancelComponent,
    ListComponent,
    DetailComponent,
    OrderFormComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MaterialModule,
    FlexLayoutModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    MatIconModule,
    NgxUiLoaderModule.forRoot(ngxUiLoaderConfig),
    SharedModule,
    DashboardModule,
    SharedQuillModule,
    MonacoModule,
    VgBufferingModule,
    VgControlsModule,
    VgCoreModule,
    VgOverlayPlayModule,
    SharedChartsModule,
  ],
  providers: [HttpClientModule, {provide: HTTP_INTERCEPTORS, useClass: TokenInterceptorInterceptor, multi: true}],
  bootstrap: [AppComponent]
})
export class AppModule { }
