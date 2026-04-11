import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { UserService } from '../../services/user.service';
import { Person } from '../../models/person.model';

@Component({
  selector: 'app-user-form-dialog',
  templateUrl: './user-form-dialog.component.html',
  styleUrls: ['./user-form-dialog.component.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatIconModule],
})
export class UserFormDialogComponent {
  userForm: FormGroup;
  isEdit: boolean;
  errorMsg: string = '';

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    public dialogRef: MatDialogRef<UserFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Person | null
  ) {
    this.isEdit = !!data;
    this.userForm = this.fb.group({
      name: [data?.name || '', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      password: ['', [Validators.required, Validators.minLength(8), this.strongPasswordValidator]],
      age: [data?.age || '', [Validators.required, Validators.min(18)]],
      email: [data?.email || '', [Validators.required, Validators.email]],
      // departmentId, projectIds can be added here if needed
    });
    if (this.isEdit) {
      this.userForm.get('password')?.clearValidators();
      this.userForm.get('password')?.updateValueAndValidity();
    }
  }

  strongPasswordValidator(control: any) {
    const value = control.value || '';
    if (!value) return null;
    const hasUpper = /[A-Z]/.test(value);
    const hasLower = /[a-z]/.test(value);
    const hasDigit = /\d/.test(value);
    const hasSpecial = /[^A-Za-z0-9]/.test(value);
    return hasUpper && hasLower && hasDigit && hasSpecial ? null : { strongPassword: true };
  }

  onSubmit() {
    if (this.userForm.invalid) return;
    const user = this.userForm.value;
    const handleError = (err: any, fallback: string) => {
      // Try to extract backend error message (ValidationException, RuntimeException, etc.)
      this.errorMsg = err?.error?.message || err?.error || err?.message || fallback;
    };
    if (this.isEdit && this.data) {
      this.userService.updateUser(this.data.id, user).subscribe({
        next: () => this.dialogRef.close(true),
        error: err => handleError(err, 'Update failed')
      });
    } else {
      this.userService.addUser(user).subscribe({
        next: () => this.dialogRef.close(true),
        error: err => handleError(err, 'Create failed')
      });
    }
  }

  onCancel() {
    this.dialogRef.close();
  }
}
