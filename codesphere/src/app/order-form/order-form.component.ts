import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {FormBuilder, FormGroup, UntypedFormGroup, Validators} from "@angular/forms";
import {GlobalConstants} from "../shared/global-constants";
import {OrderService} from "../services/payment/order.service";
import {Payment} from "../models/payment";
import {SnackbarService} from "../services/snackbar.service";

@Component({
  selector: 'app-order-form',
  templateUrl: './order-form.component.html',
  styleUrls: ['./order-form.component.scss']
})
export class OrderFormComponent implements OnInit {

  data: any;
  orderForm : any = FormGroup;
  price: number = 0;
  totalAmount: number = 0;
  randomString: string = '';
  paymentResponse: Payment | null = null;
  responseMessage: string = '';

  constructor(@Inject(MAT_DIALOG_DATA) private matDialogData: any,
              private formBuilder: FormBuilder,
              private orderService: OrderService,
              private snackbar: SnackbarService,
              ) {
    this.data = this.matDialogData.tour;
    this.price = this.matDialogData.tour.price
    this.totalAmount = this.matDialogData.tour.price
  }

  ngOnInit(): void {
    this.orderForm = this.formBuilder.group({
      quantity: [1, Validators.required],
      note: [null]
    })
  }

  keyUp(){
    this.totalAmount = this.price * this.orderForm.value.quantity;
  }

  createPayment(){
    console.log("opk")
    this.generateRandomString()
    const data = {
      tourId: this.data.id,
      productName: this.data.title,
      description: this.randomString,
      price: this.data.price,
      quantity: this.orderForm.value.quantity,
      returnUrl: 'http://localhost:4200/success',
      cancelUrl: 'http://localhost:4200/cancel'
    }
    this.orderService.createPaymentLink(data).subscribe({
      next: (response: any)=>{
        this.paymentResponse = response.data;
        if (this.paymentResponse?.checkoutUrl){
          window.location.href = this.paymentResponse?.checkoutUrl;
        }
      },
      error: (err: any)=>{
        this.responseMessage = err.error.message;
        this.snackbar.openSnackBar(this.responseMessage, GlobalConstants.error)
      }
    })
  }

  generateRandomString() {
    this.randomString = Math.random().toString(36).substring(2, 12); // Lấy 10 ký tự
  }
}
