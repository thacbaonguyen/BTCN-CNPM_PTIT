import { Component, OnInit } from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";
import {UserService} from "../../../services/user.service";
import {SnackbarService} from "../../../services/snackbar.service";
import {MatDialog, MatDialogConfig, MatDialogRef} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {NgxUiLoaderService} from "ngx-ui-loader";
import {SharedService} from "../../../services/shared.service";
import {GlobalConstants} from "../../../shared/global-constants";
import {SetPasswordComponent} from "../set-password/set-password.component";

@Component({
  selector: 'app-verify-forgot-password',
  templateUrl: './verify-forgot-password.component.html',
  styleUrls: ['./verify-forgot-password.component.scss']
})
export class VerifyForgotPasswordComponent implements OnInit {
  verifyForgotPasswordForm: any = UntypedFormGroup;
  responseMessage: any;
  email: string = '';
  otpPlus:any = '';
  array = new Array(6).fill(0);

  // resend
  countDown = 180;
  canResend:any = false;
  interval:any;

  constructor(private formBuilder: UntypedFormBuilder,
              private userService: UserService,
              private snackbar: SnackbarService,
              public matDialogRef: MatDialogRef<VerifyForgotPasswordComponent>,
              private router: Router,
              private ngxUiLoader: NgxUiLoaderService,
              private sharedService: SharedService,
              private matDialog: MatDialog) { }

  ngOnInit(): void {
    this.sharedService.currentEmail.subscribe(email => {
      this.email = email;
      console.log(this.email)
    })
    this.verifyForgotPasswordForm = this.formBuilder.group(
      {
        otp0: [null, Validators.required],
        otp1: [null, Validators.required],
        otp2: [null, Validators.required],
        otp3: [null, Validators.required],
        otp4: [null, Validators.required],
        otp5: [null, Validators.required],
        email: [this.email]
      }
    )

    this.startCountDown();
  }

  handleSubmit(){
    this.ngxUiLoader.start();
    var formData = this.verifyForgotPasswordForm.value;
    this.otpPlus = formData.otp0 + formData.otp1 + formData.otp2 + formData.otp3 + formData.otp4 + formData.otp5;
    var data = {
      otp: this.otpPlus,
      email: formData.email
    }
    console.log(data)

    this.userService.verifyForgotPassword(data).subscribe((response: any)=>{
      this.ngxUiLoader.stop();
      this.matDialogRef.close();
      this.responseMessage = response?.message;
      this.snackbar.openSnackBar(this.responseMessage, '');
      this.router.navigate(['/']);
      this.handleSetPasswordAction();
    },(error)=>{
      this.ngxUiLoader.stop();
      if (error.error?.message){
        this.responseMessage = error.error.message;
      }
      else {
        this.responseMessage = GlobalConstants.generateError;
      }
      this.snackbar.openSnackBar(this.responseMessage, GlobalConstants.error)
    })
  }

  // chuyen sang nhap o tiep theo
  moveFocus(event: any, index: number) {
    const input = event.target;
    if (input.value && index < 5) {
      input.nextElementSibling?.focus();
    }
  }

  startCountDown(){
    this.canResend = false;
    this.countDown = 180;

    this.interval = setInterval(()=>{
      if (this.countDown > 0){
        this.countDown--;
      }
      else {
        this.canResend = true;
        clearInterval(this.interval); // stop
      }
    }, 1000);
  }

  resendOTP(){
    var data = {
      email: this.email
    }
    if (!this.canResend) return;
    else {
      this.userService.resendOTP(data).subscribe((response: any)=>{
      })
    }
    this.startCountDown();
  }

  handleSetPasswordAction(){
    const matDialogConfig = new MatDialogConfig();
    matDialogConfig.width = "700px";
    this.matDialog.open(SetPasswordComponent, matDialogConfig)
  }

  protected readonly Array = Array;

}
