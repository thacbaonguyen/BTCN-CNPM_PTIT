import { Component, OnInit } from '@angular/core';
import {AuthService} from "../../../services/auth/auth.service";
import {Router} from "@angular/router";


@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {

  menuItems: any[] = [];
  userRoles: string[] = [];

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.authService.getRolesFromToken().subscribe(roles => {
      this.userRoles = roles;
      this.initializeMenu();
    });
  }

  /**
   * title: tên menu
   * roles: chỉ hiển thị ra màn hình đối với những role có trong roles
   * type : -head : thanh hiển thị. -action: các thanh chức năng
   * path: đường dẫn đến trang -> app-routing -> routeGuard -> hiển thị
   * các tài nguyên liên quan đến thành viên thì chỉ admin được truy cập
   * @private
   */
  private initializeMenu() {
    this.menuItems = [
      {
        title: 'Công cụ',
        roles: ['ADMIN', 'MANAGER'],
        type: 'head'
      },
      {
        title: 'Tổng quan',
        icon: 'assessment',
        path: '/codesphere/dashboard/overview',
        roles: ['ADMIN', 'MANAGER'], // cả admin và manager được truy cập
        type: 'action'
      },
      {
        title: 'Nội dung',
        roles: ['ADMIN', 'MANAGER'],
        type: 'head'
      },
      {
        title: 'Thành viên',
        icon: 'people',
        path: '/codesphere/dashboard/member',
        roles: ['ADMIN'], // chỉ admin được truy cập danh sách thành viên
        type: 'action'
      },
      {
        title: 'Tour du lịch',
        icon: 'play_circle_filled',
        children: [
          { title: 'Danh sách tour', path: '/codesphere/dashboard/courses/list' },
          { title: 'Chi tiết tour', path: '/codesphere/dashboard/courses/details/1' }
        ],
        roles: ['ADMIN', 'MANAGER'],
        type: 'action'
      },
    ];
  }

  hasAccess(item: any): boolean {
    return item.roles ? item.roles.some((role: string) => this.userRoles.includes(role)) : true;
  }

}
