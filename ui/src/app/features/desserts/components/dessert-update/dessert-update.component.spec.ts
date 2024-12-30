import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DessertUpdateComponent } from './dessert-update.component';

describe('DessertUpdateComponent', () => {
  let component: DessertUpdateComponent;
  let fixture: ComponentFixture<DessertUpdateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DessertUpdateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DessertUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
