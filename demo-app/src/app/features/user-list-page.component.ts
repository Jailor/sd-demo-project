import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../services/user.service';
import { Person } from '../models/person.model';
import { MatDialog } from '@angular/material/dialog';
import { UserFormDialogComponent } from '../components/user-form-dialog/user-form-dialog.component';

@Component({
  selector: 'app-user-list-page',
  templateUrl: './user-list-page.component.html',
  styleUrls: ['./user-list-page.component.scss'],
  standalone: true,
  imports: [CommonModule],
})
export class UserListPageComponent implements OnInit {
  users: Person[] = [];

  constructor(private userService: UserService, private dialog: MatDialog) {}

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.userService.getUsers().subscribe(users => this.users = users);
  }

  openAddUserDialog() {
    const dialogRef = this.dialog.open(UserFormDialogComponent, { data: null });
    dialogRef.afterClosed().subscribe(result => { if (result) this.loadUsers(); });
  }

  openEditUserDialog(user: Person) {
    const dialogRef = this.dialog.open(UserFormDialogComponent, { data: user });
    dialogRef.afterClosed().subscribe(result => { if (result) this.loadUsers(); });
  }

  deleteUser(user: Person) {
    if (confirm('Delete this user?')) {
      this.userService.deleteUser(user.id).subscribe(() => this.loadUsers());
    }
  }
}
