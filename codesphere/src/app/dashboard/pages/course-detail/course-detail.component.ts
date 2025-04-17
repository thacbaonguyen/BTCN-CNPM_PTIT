import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { CourseDetail } from 'src/app/models/course-detail';
import { CourseService } from 'src/app/services/course/course.service';
import { VgApiService } from "@videogular/ngx-videogular/core";
import { Video } from "../../../models/video";
import {DomSanitizer, SafeHtml} from "@angular/platform-browser";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {SnackbarService} from "../../../services/snackbar.service";

@Component({
  selector: 'app-course-detail',
  templateUrl: './course-detail.component.html',
  styleUrls: ['./course-detail.component.scss']
})
export class CourseDetailComponent implements OnInit {

  courseDetail: CourseDetail = <CourseDetail>{};
  responseMessage: string = '';
  id!: number;
  api!: VgApiService;
  currentVideo: Video = <Video>{};
  expandedSections: boolean[] = [];
  url: string = '';
  selectedTab: string = 'overview';

  currentTime: number = 0;
  totalDuration: number = 0;

  stars: number[] = [1, 2, 3, 4, 5];
  //
  sanitizedContent: SafeHtml | null = null;
  //
  isScrubBarVisible: boolean = true;

  constructor(
    private courseService: CourseService,
    private ngxUiLoaderService: NgxUiLoaderService,
    private router: Router,
    private route: ActivatedRoute,
    private sanitizer: DomSanitizer,
    private matDialog: MatDialog,
    private snackbar: SnackbarService
  ) {}

  ngOnInit(): void {
    if (this.route.snapshot.paramMap.get('id')) {
      this.id = parseInt(this.route.snapshot.paramMap.get('id') || '1');
    }
    this.loadCourseDetail(this.id);
  }

  loadCourseDetail(id: number) {
    this.ngxUiLoaderService.start();
    this.courseService.getCourseDetail(id).subscribe({
      next: (response: any) => {
        this.courseDetail = response.data;
        this.sanitizedContent = this.sanitizer.bypassSecurityTrustHtml(<string>this.courseDetail?.description);
        this.expandedSections = new Array(this.courseDetail.sections?.length).fill(false);
        if (this.courseDetail.sections &&
          this.courseDetail.sections.length > 0 &&
          this.courseDetail.sections[0].videos &&
          this.courseDetail.sections[0].videos.length > 0) {
          this.currentVideo = this.courseDetail.sections[0].videos[0];
        }

        this.ngxUiLoaderService.stop();
      },
      error: (err: any) => {
        console.log("error loading course details", err);
        this.ngxUiLoaderService.stop();
      }
    });
  }

  toggleSection(index: number) {
    this.expandedSections[index] = !this.expandedSections[index];
  }

  selectTab(tab: string) {
    this.selectedTab = tab;
  }

  onMouseEnter() {
    this.isScrubBarVisible = true;
  }
  onMouseLeave() {
    this.isScrubBarVisible = false;
  }

}
