import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DisplayOrderComponent } from './display-order.component';

describe('DisplayOrderComponent', () => {
  let component: DisplayOrderComponent;
  let fixture: ComponentFixture<DisplayOrderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DisplayOrderComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DisplayOrderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
