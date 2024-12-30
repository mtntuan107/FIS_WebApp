import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DessertDeleteComponent } from './dessert-delete.component';

describe('DessertDeleteComponent', () => {
  let component: DessertDeleteComponent;
  let fixture: ComponentFixture<DessertDeleteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DessertDeleteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DessertDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
