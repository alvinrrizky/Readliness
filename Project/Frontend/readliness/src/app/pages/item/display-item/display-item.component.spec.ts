import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DisplayItemComponent } from './display-item.component';

describe('DisplayItemComponent', () => {
  let component: DisplayItemComponent;
  let fixture: ComponentFixture<DisplayItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DisplayItemComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DisplayItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
