import { Component, Input, forwardRef } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-number-field',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="form-group">
      <label *ngIf="label" [for]="fieldId" class="form-label">
        {{ label }}
        <span *ngIf="required" class="text-danger">*</span>
      </label>
      <input
        [id]="fieldId"
        type="number"
        class="form-control"
        [placeholder]="placeholder"
        [disabled]="isDisabled"
        [value]="value || ''"
        [min]="min"
        [max]="max"
        (input)="onInput($event)"
      />
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
      useExisting: forwardRef(() => NumberFieldComponent),
      multi: true
    }
  ]
})
export class NumberFieldComponent implements ControlValueAccessor {
  @Input() label: string = '';
  @Input() placeholder: string = '';
  @Input() required: boolean = false;
  @Input() hint: string = '';
  @Input() min: number = 0;
  @Input() max: number = 999999999;
  @Input() fieldId: string = 'number-field-' + Math.random().toString(36).substr(2, 9);

  value: number | null = null;
  isDisabled: boolean = false;
  onChange: any = () => {};
  onTouched: any = () => {};

  onInput(event: any): void {
    this.value = event.target.value ? Number(event.target.value) : null;
    this.onChange(this.value);
  }

  writeValue(value: any): void {
    this.value = value || null;
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.isDisabled = isDisabled;
  }
}

