import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {CommonModule} from '@angular/common';
import {FlexLayoutModule} from '@angular/flex-layout';
import {DashboardComponent} from './dashboard.component';
import {DashboardRoutes} from './dashboard.routing';
import {MaterialModule} from '../shared/material-module';
import {SidebarComponent} from './component/sidebar/sidebar.component';
import {OverviewComponent} from './pages/overview/overview.component';
import {MemberComponent} from './pages/member/member.component';
import {CourseComponent} from './pages/course/course.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { DashboardHeaderComponent } from './component/dashboard-header/dashboard-header.component';

import {MarkdownPipe} from "../shared/markdown.pipe";

import {SharedQuillModule} from "../shared/quill/quill.module";

import {SharedModule} from "../shared/shared.module";
import { CourseListComponent } from './pages/tour-list/course-list.component';
import { CourseDetailComponent } from './pages/course-detail/course-detail.component';
import { ActionCourseComponent } from './component/action-course/action-course.component';
import {SharedVideogularModule} from "../shared/videogular/videogular.module";



@NgModule({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        RouterModule.forChild(DashboardRoutes),
        FormsModule,
        ReactiveFormsModule,
        SharedQuillModule,
      SharedModule,
      SharedVideogularModule
        // QuillModule.forRoot()

    ],
    exports: [
        MarkdownPipe,
    ],
    declarations: [DashboardComponent,
        SidebarComponent,
        OverviewComponent,
        MemberComponent,
        CourseComponent,
        DashboardHeaderComponent,
        MarkdownPipe,
        CourseListComponent,
        CourseDetailComponent,
        ActionCourseComponent,
    ]
})
export class DashboardModule {
}
