import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import {DashboardComponent} from "./dashboard/dashboard.component";
import {RouteGuardService} from "./services/route-guard/route-guard.service";
import {UnauthorizedComponent} from "./unauthorized/unauthorized.component";
import {OverviewComponent} from "./dashboard/pages/overview/overview.component";
import {MemberComponent} from "./dashboard/pages/member/member.component";
import { CourseListComponent } from './dashboard/pages/tour-list/course-list.component';
import { CourseDetailComponent } from './dashboard/pages/course-detail/course-detail.component';
import {CourseRsComponent} from "./course-rs/course-rs.component";
import {CourseDetailsComponent} from "./tour-details/course-details.component";
import {CartComponent} from "./cart/cart.component";
import {SuccessComponent} from "./payment/success/success.component";
import {CancelComponent} from "./payment/cancel/cancel.component";
import {ListComponent} from "./access-course/list/list.component";
import {DetailComponent} from "./access-course/detail/detail.component";

const routes: Routes = [
  {
    path: 'home',
    component: HomeComponent
  },
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },
  {
    path: 'unauthorized',
    component: UnauthorizedComponent
  },
  {
    path: 'codesphere',
    component: DashboardComponent,  // Thêm component cha
    canActivate: [RouteGuardService],
    data: {
      allowedRoles: ['ADMIN', 'MANAGER']
    },
    children: [
      {
        path: '',
        redirectTo: '/codesphere/dashboard',  // Sửa đường dẫn redirect đầy đủ
        pathMatch: 'full',
      },
      {
        path: 'dashboard',
        loadChildren: () => import('./dashboard/dashboard.module').then((m) => m.DashboardModule),
      },
      {
        path: 'dashboard/overview',
        component: OverviewComponent
      },
      {
        path: 'dashboard/member',
        component: MemberComponent,
        canActivate: [RouteGuardService],
        data: {
          allowedRoles: ['ADMIN']
        }
      },

      {
        path: 'dashboard/courses/list',
        component: CourseListComponent,
        canActivate: [RouteGuardService],
        data: {
          allowedRoles: ['ADMIN', 'MANAGER']
        }
      },
      {
        path: 'dashboard/courses/details/:id',
        component: CourseDetailComponent,
        canActivate: [RouteGuardService],
        data: {
          allowedRoles: ['ADMIN', 'MANAGER']
        }
      },
    ]
  },
  {
    path: 'tours',
    component: CourseRsComponent
  },
  {
    path: 'tour/tour-details/:id/:thumbnail',
    component: CourseDetailsComponent
  },
  {
    path: 'cart',
    component: CartComponent
  },
  {
    path: 'success',
    component: SuccessComponent,
  },
  {
    path: 'cancel',
    component: CancelComponent,
  },
  {
    path: 'my-courses',
    component: ListComponent,
  },
  {
    path: 'my-courses/:id/:thumbnail',
    component: DetailComponent,
  },
  {
    path: '**',
    redirectTo: 'home'
  },


];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
