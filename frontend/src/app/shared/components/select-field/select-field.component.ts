import { Component, Input, forwardRef } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

interface SelectOption {
  value: string | number;
  label: string;
}

@Component({
  selector: 'app-select-field',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="form-group">
      <label *ngIf="label" [for]="fieldId" class="form-label">
        {{ label }}
        <span *ngIf="required" class="text-danger">*</span>
      </label>
      <select
        [id]="fieldId"
        class="form-control"
        [disabled]="isDisabled"
        [value]="value || ''"
        (change)="onChange($event)"
      >
        <option value="">{{ placeholder || 'Select...' }}</option>
        <option *ngFor="let opt of options" [value]="opt.value">
          {{ opt.label }}
        </option>
      </select>
      <small *ngIf="hint" class="form-text text-muted">{{ hint }}</small>
    </div>
  `,
  styles: [`
    .form-group {
      margin-bottom: 1rem;
    }
    .form-label {
      display: block;
      margin-bottom: 0.5rem;
      font-weight: 500;
    }
    .form-control {
      width: 100%;
      padding: 0.5rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 1rem;
    }
    .form-control:disabled {
      background-color: #f5f5f5;
      cursor: not-allowed;
    }
    .form-text {
      display: block;
      margin-top: 0.25rem;
    }
    .text-danger {
      color: #dc3545;
    }
  `],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SelectFieldComponent),
      multi: true
    }
  ]
})
export class SelectFieldComponent implements ControlValueAccessor {
  @Input() label: string = '';
  @Input() placeholder: string = '';
  @Input() required: boolean = false;
  @Input() hint: string = '';
  @Input() options: SelectOption[] = [];
  @Input() fieldId: string = 'select-field-' + Math.random().toString(36).substr(2, 9);

  value: string | number = '';
  isDisabled: boolean = false;
  changeCallback: any = () => {};
  touchedCallback: any = () => {};

  onChange(event: any): void {
    this.value = event.target.value;
    this.changeCallback(this.value);
  }

  writeValue(value: any): void {
    this.value = value || '';
  }

  registerOnChange(fn: any): void {
    this.changeCallback = fn;
  }

  registerOnTouched(fn: any): void {
    this.touchedCallback = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.isDisabled = isDisabled;
  }
}

