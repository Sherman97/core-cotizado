import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-stepper',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="wizard-steps">
      <div class="wizard-step" [class.active]="isActiveStep(step)" *ngFor="let step of steps">
        {{ getStepLabel(step) }}
      </div>
    </div>
  `
})
export class StepperComponent {
  @Input() steps: Array<string | { value: string; label: string }> = [];
  @Input() current = '';

  isActiveStep(step: string | { value: string; label: string }): boolean {
    return (typeof step === 'string' ? step : step.value) === this.current;
  }

  getStepLabel(step: string | { value: string; label: string }): string {
    return typeof step === 'string' ? step : step.label;
  }
}
