import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-stepper',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="wizard-steps">
      <div class="wizard-step" [class.active]="step === current" *ngFor="let step of steps">
        {{ step }}
      </div>
    </div>
  `
})
export class StepperComponent {
  @Input() steps: string[] = [];
  @Input() current = '';
}
